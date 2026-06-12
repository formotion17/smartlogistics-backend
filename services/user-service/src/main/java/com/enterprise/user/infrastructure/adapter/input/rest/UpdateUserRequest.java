package com.enterprise.user.infrastructure.adapter.input.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserRequest(
    @NotBlank(message = "El nombre no puede estar vacío")
    String name,

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El formato del email no es válido")
    String email
) {}