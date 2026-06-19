package com.enterprise.user.infrastructure.adapter.output.persistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.javers.core.Javers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserAudEntity;
import com.enterprise.user.infrastructure.adapter.output.persistence.mapper.UserMapper;
import com.enterprise.user.infrastructure.adapter.output.persistence.repository.SpringDataUserAudRepository;
import com.enterprise.user.infrastructure.adapter.output.persistence.repository.SpringDataUserRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Adaptador de persistencia que implementa el puerto de salida {@link UserRepositoryPort}.
 * <p>
 * Refactorizado para delegar la lógica de mapeo a {@link UserMapper}, 
 * mejorando la mantenibilidad y cumpliendo con el principio de responsabilidad única.
 * </p>
 * <p>
 * <b>Modificación Fase 2:</b> Se incorpora el soporte de Redis mediante anotaciones declarativas
 * para optimizar las lecturas por ID y por Email, gestionando de forma segura la invalidación.
 * Adicionalmente, coordina de forma transaccional la inserción en la tabla espejo 'user_aud'
 * soportando actualizaciones parciales de campos (Null-Safe Updates).
 * </p>
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private static final Logger log = LoggerFactory.getLogger(UserPersistenceAdapter.class);
    private final SpringDataUserRepository springDataUserRepository;
    private final SpringDataUserAudRepository springDataUserAudRepository; // Inyectamos el repositorio histórico
    private final UserMapper userMapper;
    private final Javers javers; // Inyectamos Javers para auditoría de cambios

    /**
     * Constructor inyectado por Spring.
     * @param springDataUserRepository Repositorio de Spring Data.
     * @param springDataUserAudRepository Repositorio histórico de auditoría.
     * @param userMapper Mapper para la conversión entre dominio y entidad.
     */
    public UserPersistenceAdapter(SpringDataUserRepository springDataUserRepository, 
                                  SpringDataUserAudRepository springDataUserAudRepository, 
                                  UserMapper userMapper,
                                  Javers javers) {
        this.springDataUserRepository = springDataUserRepository;
        this.springDataUserAudRepository = springDataUserAudRepository;
        this.userMapper = userMapper;
        this.javers = javers;
    }

    /**
     * Guarda un usuario aplicando una estrategia de reactivación si el email 
     * ya existía previamente bajo un estado inactivo (Soft Delete) o una actualización
     * parcial segura si el ID ya existe en el sistema.
     * <p>
     * <b>Mecanismo Redis:</b> Al guardar o actualizar (o resucitar), debemos desalojar las entradas de 
     * la memoria RAM. Usamos el objeto '#result' (el usuario ya persistido y devuelto) para obtener 
     * de forma segura el UUID y el Email final, limpiando ambos contenedores.
     * </p>
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "usersById", key = "#result.id", condition = "#result != null"),
        @CacheEvict(value = "usersByEmail", key = "#result.email", condition = "#result != null")
    }) //@Caching permite agrupar múltiples operaciones de desalojo (Evict) en un solo método
    public User save(User user) {

        UserEntity entityToSave;
        String actionType;

        // Comprobamos si es una actualización parcial de un usuario existente por ID
        Optional<UserEntity> existingUserByIdOpt = Optional.empty();
        if (user.getId() != null) {
            existingUserByIdOpt = springDataUserRepository.findById(user.getId());
        }

        if (existingUserByIdOpt.isPresent()) {
            // ESTRATEGIA UPDATE PARCIAL: Si el usuario ya existe por ID, cargamos el registro de la BD
            //entityToSave= existingUserByIdOpt.get(); // Guardamos el estado previo para la auditoría de cambios
            //UserEntity oldEntity = existingUserByIdOpt.get();

            UserEntity oldEntity = existingUserByIdOpt.get();
            entityToSave = userMapper.cloneEntity(oldEntity);
            
            // Solo sobreescribimos los campos que NO vengan nulos ni vacíos en la petición del cliente
            if (user.getName() != null && !user.getName().isBlank()) entityToSave.setName(user.getName());
            if (user.getEmail() != null && !user.getEmail().isBlank()) entityToSave.setEmail(user.getEmail());
            if (user.getPhone() != null) entityToSave.setPhone(user.getPhone());
            if (user.getPassword() != null && !user.getPassword().isBlank()) entityToSave.setPassword(user.getPassword());
            if (user.getStatus() != null) entityToSave.setStatus(user.getStatus().name());
            
            actionType = "UPDATE";

            // [NUEVO] DETECCIÓN DE DELTA CON JAVERS
            // Comparamos el estado previo (oldEntity) con el nuevo (entityToSave)
            var diff = javers.compare(oldEntity, entityToSave);
            if (diff.hasChanges()) {
                log.debug("DEBUG - Delta detected: " + diff.prettyPrint());
                // Llamamos a registrarHistorico pasando los cambios detectados
                for (org.javers.core.diff.Change change : diff.getChanges()) {
                    registrarHistorico(entityToSave, "UPDATE_DELTA: " + change.toString());
                }
            }
            
        } else {
            // 1. Buscamos en la BD usando la query nativa si existe el registro por EMAIL (activo o inactivo)
            Optional<UserEntity> ghostUserOpt = springDataUserRepository.findAnyByEmailNative(user.getEmail());

            if (ghostUserOpt.isPresent()) {
                // 2. ¡ESTRATEGIA RESURRECCIÓN! Si el email ya existía pero estaba inactivo,
                // reutilizamos su misma fila y su mismo ID para evitar violar el índice UNIQUE de PostgreSQL.
                entityToSave = ghostUserOpt.get();
                
                // Le sobreescribimos los nuevos datos
                entityToSave.setName(user.getName());
                entityToSave.setPhone(user.getPhone());
                entityToSave.setPassword(user.getPassword()); // Nueva contraseña encriptada
                entityToSave.setStatus(user.getStatus().name()); // Rol reasignado (ej: USER)
                entityToSave.setActive(true); // Lo volvemos a activar
                

                actionType = "UPDATE"; // Al reutilizar la fila, es una actualización histórica
            } else {
                // 3. Si es un email 100% nuevo en la plataforma, mapeamos a entidad de forma normal
                entityToSave = userMapper.toEntity(user);
                entityToSave.setActive(true); // Nace activo por defecto

                actionType = "INSERT"; // Es un nacimiento puro en el sistema
            }
        }

        // 4. Guardamos (Spring Data JPA hará el cambio de forma unificada)
        UserEntity savedEntity = springDataUserRepository.save(entityToSave);

        // 🚀 ORQUESTRACIÓN SENIOR: Grabamos el Snapshot Histórico de forma segura fuera del ciclo de Hibernate
        if ("INSERT".equals(actionType)) {
            registrarHistorico(savedEntity, actionType);
        }
        
        // 5. Devolvemos el resultado mapeado al Dominio
        return userMapper.toDomain(savedEntity);
    }

    /**
     * Busca un usuario por correo, mapeando el resultado si existe.
     */
    @Override
    @Cacheable(value = "usersByEmail", key = "#email", unless = "#result == null") // 🟢 Evita cachear nulos
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
            .map(userMapper::toDomain);
    }

    /**
     * Busca un usuario por ID, mapeando el resultado si existe.
     */
    @Override
    @Cacheable(value = "usersById", key = "#id", unless = "#result == null") // 🟢 Evita cachear nulos
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
            .map(userMapper::toDomain);
    }

    /**
     * Elimina un usuario por ID (Aplica Soft Delete mutando el flag activo).
     */
    @Override
    @Caching(evict = {
        @CacheEvict(value = "usersById", key = "#id"),
        @CacheEvict(value = "usersByEmail", allEntries = true)
    })
    public void deleteById(UUID id) {
        springDataUserRepository.findById(id).ifPresent(userEntity -> {
            springDataUserRepository.deleteById(id); 
            userEntity.setActive(false); 
            registrarHistorico(userEntity, "DELETE_LOGIC");
        });
    }

    /**
     * Recupera una página de usuarios de la base de datos y los mapea al modelo de dominio.
     */
    @Override
    public Page<User> findAll(Pageable pageable) {
        Page<UserEntity> entityPage = springDataUserRepository.findAll(pageable); 
        return entityPage.map(userMapper::toDomain); 
    }

    /**
     * Método auxiliar privado encargado de extraer el operador autenticado del token JWT
     * y persistir el clon histórico estructurado en la tabla 'user_aud'.
     */
    private void registrarHistorico(UserEntity entity, String accion) {
        String operador = "SYSTEM";
        String ipAddress = com.enterprise.user.infrastructure.util.NetworkUtils.getLocalIpAddress();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() != null) {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof com.enterprise.user.domain.model.User) {
                // 🚀 SOLUCIÓN AL IDENTIFICADOR: En lugar de sacar el email, extraemos el UUID 
                // inmutable de dominio y lo convertimos a String para la columna histórica.
                UUID actorUuid = ((com.enterprise.user.domain.model.User) principal).getId();
                operador = (actorUuid != null) ? actorUuid.toString() : "SYSTEM";
            } else {
                operador = authentication.getName(); // Fallback estándar de Spring Security
            }
        }
        
        UserAudEntity auditoriaHistorica = new UserAudEntity(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getStatus(),
                entity.getPhone(),
                entity.isActive(),
                accion,
                operador,
                LocalDateTime.now(),
                ipAddress
        );

        springDataUserAudRepository.save(auditoriaHistorica);
    }

    /**
     * 🚀 IMPLEMENTACIÓN LIMPIA DE ACCESOS:
     * Delega la lógica en el motor centralizado de auditoría, permitiendo que
     * la IP sea capturada dinámicamente de la petición HTTP activa.
     */
    @Override
    public void registrarAccesoLogin(User user) {
        // 1. Mapeamos a entidad para obtener el estado actual
        UserEntity entity = userMapper.toEntity(user);
        
        // 2. Delegamos al método centralizado (que ya sabe extraer la IP automáticamente)
        registrarHistorico(entity, "LOGIN");
    }
    
}