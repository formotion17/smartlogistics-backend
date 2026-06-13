package com.enterprise.user.application.ports.input;

import com.enterprise.user.domain.model.User;

/**
 * Puerto de entrada (Input Port) para el caso de uso de creación de usuarios.
 * <p>
 * Define el contrato que debe implementar cualquier servicio de aplicación 
 * encargado de gestionar el registro de un nuevo usuario en el sistema.
 * Esto asegura que el núcleo de negocio sea independiente de las capas de infraestructura.
 * </p>
 */
public interface CreateUserUseCase {

    /**
     * Ejecuta el proceso de creación de un usuario.
     *
     * @param command Objeto que contiene los datos validados para la creación del usuario.
     * @return El objeto de dominio {@link User} creado y persistido.
     */
    User createUser(CreateUserCommand command);
}