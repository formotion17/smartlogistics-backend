package com.enterprise.user.infrastructure.adapter.output.persistence.repository;

import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserAudEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio Spring Data JPA para la gestión exclusiva de inserciones 
 * en la tabla histórica de auditoría (user_aud).
 */
@Repository
public interface SpringDataUserAudRepository extends JpaRepository<UserAudEntity, Long> {
}