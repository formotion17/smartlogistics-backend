package com.enterprise.user.infrastructure.adapter.output.persistence.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad de persistencia que representa la tabla 'users' en la base de datos.
 * <p>
 * Esta clase es exclusiva de la capa de infraestructura. Su único propósito es
 * facilitar el mapeo ORM (Object-Relational Mapping) mediante JPA.
 * No debe contener lógica de negocio, solo anotaciones de mapeo y estructura de datos.
 * </p>
 */
@Entity
@Table(name = "users")
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

    /**
     * Constructor por defecto requerido por la especificación JPA para la
     * instanciación de entidades mediante reflexión.
     */
    public UserEntity() {
    }

    /**
     * Constructor completo para inicializar la entidad desde el adaptador de persistencia.
     * * @param id Identificador único.
     * @param name Nombre del usuario.
     * @param email Correo electrónico.
     * @param status Estado del usuario (almacenado como String).
     * @param createdAt Fecha y hora de creación.
     * @param phone Número de teléfono.
     * * @param password Contraseña encriptada.
     */
    public UserEntity(UUID id, String name, String email, String status, LocalDateTime createdAt, String phone, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
        this.phone = phone;
        this.password = password;
    }

   // Getters y Setters

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

}
