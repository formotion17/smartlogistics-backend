package com.enterprise.user.domain.event;

import java.util.UUID;

/**
 * Evento de dominio inmutable que representa que un usuario
 * se ha registrado en el sistema.
 */
public record UserRegisteredEvent(
    UUID id,
    String name,
    String email
) {}