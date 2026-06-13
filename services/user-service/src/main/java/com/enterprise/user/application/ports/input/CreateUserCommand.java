package com.enterprise.user.application.ports.input;

/**
 * Command que encapsula los datos necesarios para ejecutar la acción de 
 * creación de un nuevo usuario.
 * <p>
 * Este record actúa como un mensaje que transporta la información desde el 
 * adaptador de entrada hacia el caso de uso ({@code CreateUserUseCase}).
 * Garantiza la inmutabilidad de los datos durante su tránsito en la capa de aplicación.
 * </p>
 *
 * @param name  Nombre del usuario a crear.
 * @param email Correo electrónico del usuario.
 * @param phone Número de teléfono del usuario.
 */
public record CreateUserCommand(
    String name,
    String email,
    String phone
){}
