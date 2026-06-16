package com.enterprise.user.infrastructure.adapter.input.rest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO para capturar las credenciales del usuario en el endpoint de login.
 */
public record LoginRequest(
    
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid Email format")
    String email,

    @NotBlank(message = "Password cannot be empty")
    String password
) {}