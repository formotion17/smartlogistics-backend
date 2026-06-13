package com.enterprise.user.infrastructure.adapter.output.persistence;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.domain.model.UserStatus;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import com.enterprise.user.infrastructure.adapter.output.persistence.repository.SpringDataUserRepository;

@Component
public class UserPersistenceAdapter implements UserRepositoryPort {

    private final SpringDataUserRepository springDataUserRepository;

    public UserPersistenceAdapter(SpringDataUserRepository springDataUserRepository) {
        this.springDataUserRepository = springDataUserRepository;
    }

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

    @Override
    public void deleteById(java.util.UUID id) {
        springDataUserRepository.deleteById(id);
    }
}
