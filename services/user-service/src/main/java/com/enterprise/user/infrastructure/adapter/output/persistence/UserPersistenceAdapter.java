package com.enterprise.user.infrastructure.adapter.output.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

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
}