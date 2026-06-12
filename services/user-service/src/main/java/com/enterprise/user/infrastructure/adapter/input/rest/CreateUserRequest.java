package com.enterprise.user.infrastructure.adapter.input.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
    @NotNull(message = "El nombre es obligatorio para completar el formulario")
    @Pattern(regexp = ".*\\S.*", message = "El nombre no puede estar vacío")
    String name,

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    String email
) {}