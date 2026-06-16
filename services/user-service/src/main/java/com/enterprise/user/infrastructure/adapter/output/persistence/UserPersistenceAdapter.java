package com.enterprise.user.infrastructure.adapter.output.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.enterprise.user.infrastructure.adapter.output.persistence.mapper.UserMapper;
import com.enterprise.user.infrastructure.adapter.output.persistence.repository.SpringDataUserRepository;

/**
 * Adaptador de persistencia que implementa el puerto de salida {@link UserRepositoryPort}.
 * <p>
 * Refactorizado para delegar la lógica de mapeo a {@link UserMapper}, 
 * mejorando la mantenibilidad y cumpliendo con el principio de responsabilidad única.
 * </p>
 */
@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;
    private final UserMapper userMapper;

    /**
     * Constructor inyectado por Spring.
     * @param springDataUserRepository Repositorio de Spring Data.
     * @param userMapper Mapper para la conversión entre dominio y entidad.
     */
    public UserPersistenceAdapter(SpringDataUserRepository springDataUserRepository, UserMapper userMapper) {
        this.springDataUserRepository = springDataUserRepository;
        this.userMapper = userMapper;
    }

    /**
     * Persiste un usuario utilizando el Mapper para la conversión.
     */
    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(user);
        UserEntity savedEntity = springDataUserRepository.save(entity);
        return userMapper.toDomain(savedEntity);
    }

    /**
     * Busca un usuario por correo, mapeando el resultado si existe.
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return springDataUserRepository.findByEmail(email)
            .map(userMapper::toDomain);
    }

    /**
     * Busca un usuario por ID, mapeando el resultado si existe.
     */
    @Override
    public Optional<User> findById(UUID id) {
        return springDataUserRepository.findById(id)
            .map(userMapper::toDomain);
    }

    /**
     * Elimina un usuario por ID.
     */
    @Override
    public void deleteById(UUID id) {
        springDataUserRepository.deleteById(id);
    }

    /**
     * Recupera una página de usuarios de la base de datos y los mapea al modelo de dominio.
     * <p>
     * Spring Data JPA nos devuelve un Page<UserEntity>. Usamos la función .map() de la interfaz Page
     * para aplicar nuestro UserMapper a cada uno de los elementos internamente, transformando 
     * toda la página a Page<User> de forma limpia y eficiente.
     * </p>
     *
     * @param pageable Parámetros de paginación interceptados por el controlador.
     * @return Página de usuarios del modelo de dominio.
     */
    @Override
    public Page<User> findAll(Pageable pageable) {
        // 1. Buscamos en la BD usando el repositorio de Spring Data (devuelve entidades)
        // OJO: Asegúrate de que el nombre de tu variable repositorio sea correcto (ej: userRepository, jpaUserRepository, etc.)
        Page<UserEntity> entityPage = springDataUserRepository.findAll(pageable); 

        // 2. Mapeamos cada UserEntity a User (Dominio)
        // OJO: Asegúrate de que el nombre de tu mapper sea correcto
        return entityPage.map(userMapper::toDomain); 
    }
}