package com.enterprise.user.infrastructure.adapter.input.rest;

import com.enterprise.user.domain.model.UserStatus;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Record que representa el cuerpo de la petición (request body) para la actualización de un usuario.
 * <p>
 * Se utiliza para validar los datos de entrada antes de que sean procesados por el
 * controlador. Las anotaciones de validación de Jakarta aseguran que la API solo
 * reciba datos con el formato y contenido esperado.
 * </p>
 *
 * @param name  Nombre completo del usuario (no puede estar en blanco).
 * @param email Correo electrónico del usuario (debe tener formato válido).
 * @param phone Número de teléfono (debe tener entre 9 y 15 dígitos numéricos).
 */
public record UpdateUserRequest(
    String name,

    @Email(message = "El formato del email no es válido")
    String email,

    @Pattern(regexp = "^[0-9]{9,15}$", message = "El teléfono debe contener entre 9 y 15 dígitos")
    String phone,

    UserStatus status,

    String password
) {}