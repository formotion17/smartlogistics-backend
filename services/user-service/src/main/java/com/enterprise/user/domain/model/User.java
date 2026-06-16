package com.enterprise.user.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad de dominio que representa a un usuario dentro del sistema.
 * <p>
 * Esta clase encapsula la lógica de negocio y las reglas de validación de los datos.
 * Es agnóstica a cualquier framework o tecnología de persistencia, cumpliendo con
 * el principio de aislamiento del núcleo de la Arquitectura Hexagonal.
 * </p>
 */
public class User{
    
    private UUID id;
    private String name;
    private String email;
    private UserStatus status;
    private LocalDateTime createdAt;
    private String phone;
    private String password;

    /**
     * Constructor de la entidad User.
     * Realiza validaciones inmediatas sobre los datos críticos de negocio.
     *
     * @param id Identificador único.
     * @param name Nombre del usuario.
     * @param email Correo electrónico.
     * @param status Estado actual del usuario.
     * @param createdAt Fecha y hora de creación.
     * @param phone Número de teléfono.
     * @param password Contraseña en texto plano (debe ser encriptada antes de persistir).
     * @throws IllegalArgumentException si los datos de entrada violan reglas de negocio.
     */
    public User(UUID id, String name, String email, UserStatus status, LocalDateTime createdAt, String phone, String password){
        validateName(name);
        validateEmail(email);

        this.id=id;
        this.name=name;
        this.email=email;
        this.status=status;
        this.createdAt=createdAt;
        this.phone=phone;
        this.password=password;
    }

    /**
     * Valida que el nombre no sea nulo ni esté en blanco.
     */
    private void validateName(String name){
        if(name==null || name.isBlank()){
            throw new IllegalArgumentException("Name cannot be empty");
        }
    }

    /**
     * Valida que el formato del email sea básico (debe contener '@').
     */
    private void validateEmail(String email){
        if(email == null || !email.contains(("@"))){
            throw new IllegalArgumentException("Invalid Email");
        }
    }

    // --- GETTERS ---

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public UserStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }

    // --- SETTERS (Necesarios para el estado mutable durante la lógica de actualización) ---

    public void setName(String name) {
        validateName(name); // Re-validamos al modificar
        this.name = name;
}

    public void setEmail(String email) {
        validateEmail(email); // Re-validamos al modificar
        this.email = email;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
