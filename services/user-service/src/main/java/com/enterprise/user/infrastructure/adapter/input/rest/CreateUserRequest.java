package com.enterprise.user.infrastructure.adapter.input.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Record que define el contrato de entrada para la creación de un nuevo usuario.
 * <p>
 * Este DTO encapsula los datos requeridos para registrar un usuario en el sistema.
 * Aplica restricciones de validación mediante anotaciones de Jakarta para asegurar 
 * que los datos recibidos desde el cliente (Adaptador de entrada) cumplan con 
 * el formato requerido antes de ser enviados al caso de uso.
 * </p>
 *
 * @param name  Nombre del usuario. Se valida que no sea nulo ni contenga solo espacios en blanco.
 * @param email Correo electrónico. Se valida que no esté vacío y cumpla con el formato estándar de email.
 * @param phone Número de teléfono. Se valida que no esté vacío y cumpla con una expresión regular de 9 a 15 dígitos numéricos.
 * 
 */
public record CreateUserRequest(
    @NotNull(message = "El nombre es obligatorio para completar el formulario")
    @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar vacío")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email,

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe contener entre 9 y 15 dígitos")
    String phone,

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    String password
) {}