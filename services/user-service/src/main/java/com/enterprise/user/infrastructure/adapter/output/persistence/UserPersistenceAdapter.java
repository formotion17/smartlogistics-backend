package com.enterprise.user.infrastructure.adapter.output.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.domain.model.UserStatus;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.enterprise.user.infrastructure.adapter.output.persistence.repository.SpringDataUserRepository;

/**
 * Adaptador de persistencia que implementa el puerto de salida {@link UserRepositoryPort}.
 * <p>
 * Esta clase actúa como puente entre la lógica de negocio y la capa de persistencia (JPA/Hibernate).
 * Su responsabilidad es realizar el mapeo (traducción) entre el modelo de dominio {@link User}
 * y la entidad de base de datos {@link UserEntity}.
 * </p>
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    /**
     * Constructor inyectado por Spring para el repositorio de datos.
     * @param springDataUserRepository Repositorio de Spring Data JPA.
     */
    public UserPersistenceAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

    /**
     * Persiste un usuario en la base de datos realizando la conversión de dominio a entidad.
     * @param user Modelo de usuario del dominio.
     * @return El usuario guardado, mapeado de vuelta al dominio.
     */
    @Override
    public User save(User user) {
        // 1. Mapear de Dominio a Entidad de base de datos
        UserEntity userEntity = new UserEntity(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus().name(),
            user.getCreatedAt(),
            user.getPhone()
        );

        // 2. Guardar usando el repositorio de Spring Data JPA
        UserEntity savedEntity = springDataUserRepository.save(userEntity);

        // 3. Mapear de Entidad de base de datos a Dominio y devolver
        return new User(
            savedEntity.getId(),
            savedEntity.getName(),
            savedEntity.getEmail(),
            UserStatus.valueOf(savedEntity.getStatus()),
            savedEntity.getCreatedAt(),
            savedEntity.getPhone()
        );

    }

    /**
     * Busca un usuario por su correo electrónico.
     * @param email Correo electrónico a buscar.
     * @return Un Optional con el usuario si existe, o vacío.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
            .map(entity -> new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getPhone()
            ));
    }

    /**
     * Busca un usuario por su identificador único (UUID).
     * @param id UUID del usuario.
     * @return Un Optional con el usuario si existe, o vacío.
     */
    @Override
    public Optional<User> findById(java.util.UUID id) {
        return springDataUserRepository.findById(id)
            .map(entity -> new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                UserStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getPhone()
            ));
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     * @param id UUID del usuario a eliminar.
     */
    @Override
    public void deleteById(java.util.UUID id) {
        springDataUserRepository.deleteById(id);
    }
}
