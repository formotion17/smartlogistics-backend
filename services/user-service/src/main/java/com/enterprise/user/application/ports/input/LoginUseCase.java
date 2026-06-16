package com.enterprise.user.application.ports.input;

/**
 * Puerto de entrada para el caso de uso de autenticación.
 */
public interface LoginUseCase {
    String login(String email, String password);
}