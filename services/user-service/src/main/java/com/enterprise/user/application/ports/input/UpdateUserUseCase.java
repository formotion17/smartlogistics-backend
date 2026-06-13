package com.enterprise.user.application.ports.input;

import com.enterprise.user.domain.model.User;

/**
 * Puerto de entrada (Input Port) para la actualización de usuarios.
 * <p>
 * Define el contrato que debe seguir cualquier implementación del caso de uso 
 * de actualización. Al utilizar este puerto, desacoplamos la capa de entrada 
 * (como un controlador REST) de la lógica de negocio real.
 * </p>
 */
public interface UpdateUserUseCase {
    
    /**
     * Procesa la actualización de un usuario en el sistema.
     *
     * @param command Objeto que contiene los nuevos datos para actualizar el usuario.
     * @return El usuario actualizado tras aplicar la lógica de negocio.
     */
    User updateUser(UpdateUserCommand command);
}
