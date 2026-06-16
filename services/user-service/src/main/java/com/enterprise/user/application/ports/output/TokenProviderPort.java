package com.enterprise.user.application.ports.output;

import com.enterprise.user.domain.model.User;

/**
 * Puerto de salida para la generación y validación de tokens de acceso.
 * Define el contrato que debe cumplir cualquier tecnología de seguridad (JWT, etc.).
 */
public interface TokenProviderPort {

    String generateToken(User user);

    String extractUsername(String token);

    boolean isTokenValid(String token, User user);
}