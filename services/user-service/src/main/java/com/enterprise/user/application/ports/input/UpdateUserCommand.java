package com.enterprise.user.application.ports.input;

import java.util.UUID;

import com.enterprise.user.domain.model.UserStatus;

/**
 * Command que encapsula los datos necesarios para ejecutar la acción de 
 * actualización de un usuario.
 * <p>
 * Este record actúa como un mensaje que transporta la información desde el 
 * adaptador de entrada hacia el caso de uso ({@code UseCase}). Garantiza 
 * la inmutabilidad de los datos durante su tránsito en la capa de aplicación.
 * </p>
 *
 * @param id    Identificador único del usuario a actualizar.
 * @param name  Nuevo nombre del usuario.
 * @param email Nuevo correo electrónico.
 * @param phone Nuevo número de teléfono.
 */
public record UpdateUserCommand(
    UUID id,
    String name,
    String email,
    String phone,
    String password,
    UserStatus status
) {}