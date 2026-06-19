package com.enterprise.user.infrastructure.adapter.output.persistence.mapper;

import com.enterprise.user.domain.model.User;
import com.enterprise.user.domain.model.UserStatus;
import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper para la conversión entre el modelo de dominio {@link User}
 * y la entidad de persistencia {@link UserEntity}.
 * <p>
 * Centraliza la lógica de mapeo para mantener los adaptadores limpios
 * y seguir el Principio de Responsabilidad Única.
 * </p>
 */
@Component
public class UserMapper {

    /**
     * Convierte una entidad de base de datos a un modelo de dominio.
     * @param entity La entidad JPA.
     * @return El modelo de dominio equivalente.
     */
    public User toDomain(UserEntity entity) {
        return new User(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            UserStatus.valueOf(entity.getStatus()),
            entity.getCreatedAt(),
            entity.getPhone(),
            entity.getPassword()
        );
    }

    /**
     * Convierte un modelo de dominio a una entidad de base de datos.
     * @param user El modelo de dominio.
     * @return La entidad JPA equivalente.
     */
    public UserEntity toEntity(User user) {
        return new UserEntity(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getStatus().name(),
            user.getCreatedAt(),
            user.getPhone(),
            user.getPassword()
        );
    }

    public UserEntity cloneEntity(UserEntity source) {
        if (source == null) return null;
        
        UserEntity target = new UserEntity();
        // Copiamos campo a campo (id, name, email, etc.)
        target.setId(source.getId());
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setPhone(source.getPhone());
        target.setPassword(source.getPassword());
        target.setStatus(source.getStatus());
        target.setActive(source.isActive());
        // ... copia aquí cualquier otro campo que tengas en tu entidad
        
        return target;
    }
}