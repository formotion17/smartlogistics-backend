package com.enterprise.user.infrastructure.adapter.output.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.enterprise.user.infrastructure.adapter.output.persistence.entity.UserEntity;

/**
 * Repositorio de Spring Data JPA para la entidad {@link UserEntity}.
 * <p>
 * Esta interfaz es un componente puramente técnico que extiende {@link JpaRepository}
 * para proporcionar operaciones de acceso a datos (CRUD) sobre la base de datos relacional.
 * </p>
 * <p>
 * Nota: Aunque esta interfaz vive en la capa de infraestructura, su uso está restringido
 * exclusivamente al {@link com.enterprise.user.infrastructure.adapter.output.persistence.UserPersistenceAdapter},
 * garantizando que la lógica de negocio nunca dependa directamente de Spring Data.
 * </p>
 */
@Repository
public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {
    
    /**
     * Busca un usuario por su dirección de correo electrónico.
     * <p>
     * Spring Data JPA deriva automáticamente la implementación de este método 
     * mediante la convención de nombres de los atributos de la entidad.
     * </p>
     * * @param email El correo electrónico a buscar.
     * @return Un {@link Optional} que contiene la entidad si se encuentra, o vacío en caso contrario.
     */
    Optional<UserEntity> findByEmail(String email);
}
