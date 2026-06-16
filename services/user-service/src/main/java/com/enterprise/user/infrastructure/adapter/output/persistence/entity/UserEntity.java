package com.enterprise.user.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

// <--- NUEVOS IMPORTS PARA EL SOFT DELETE DE HIBERNATE 6 --->
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * Entidad de persistencia que representa la tabla 'users' en la base de datos.
 * <p>
 * Esta clase es exclusiva de la capa de infraestructura. Su único propósito es
 * facilitar el mapeo ORM (Object-Relational Mapping) mediante JPA.
 * </p>
 * <p>
 * Regla de Infraestructura: Incorpora "Soft Delete" (Borrado Lógico) global. 
 * Cualquier operación de borrado físico se interceptará y transformará en una actualización de estado.
 * Asimismo, todas las consultas filtrarán automáticamente los registros inactivos.
 * </p>
 */
@Entity
@Table(name = "users", indexes = {
    // Mapeamos el mismo nombre y columna que pusimos en el archivo de Flyway
    @Index(name = "idx_users_email", columnList = "email")
})
// Intercepta repository.delete(user) y ejecuta un UPDATE en su lugar
@SQLDelete(sql = "UPDATE users SET active = false WHERE id = ?")
// Hibernate 6: Añade automáticamente "WHERE active = true" a todas las consultas de selección (GET/findAll)
@SQLRestriction("active = true")
public class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "phone")
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    // ¿Por qué lo ponemos? Flag mecánico de control para el borrado lógico en la base de datos
    @Column(nullable = false)
    private boolean active = true;

    /**
     * Constructor por defecto requerido por la especificación JPA para la
     * instanciación de entidades mediante reflexión.
     */
    public UserEntity() {
    }

    /**
     * Constructor original (7 parámetros).
     * <p>
     * DISEÑO SEGURO: Mantenemos este constructor intacto para que tu UserMapper actual 
     * no falle al compilar. Por defecto, asume que el usuario está activo.
     * </p>
     */
    public UserEntity(UUID id, String name, String email, String status, LocalDateTime createdAt, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.phone = phone;
        this.password = password;
        this.active = true; // Valor por defecto
    }

    /**
     * Constructor maestro sobrecargado (8 parámetros).
     * Permite la reconstrucción completa del objeto incluyendo su estado de borrado lógico.
     */
    public UserEntity(UUID id, String name, String email, String status, LocalDateTime createdAt, String phone, String password, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.phone = phone;
        this.password = password;
        this.active = active;
    }

    // ===================================================================
    // GETTERS Y SETTERS
    // ===================================================================

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}