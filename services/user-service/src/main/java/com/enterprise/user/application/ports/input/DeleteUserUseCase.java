package com.enterprise.user.application.ports.input;

import java.util.UUID;

/**
 * Puerto de entrada (Input Port) para la eliminación de usuarios.
 * <p>
 * Define el contrato que debe cumplir la implementación del caso de uso de eliminación.
 * Esto asegura que cualquier adaptador que invoque esta operación lo haga a través de
 * una interfaz estándar, manteniendo el desacoplamiento.
 * </p>
 */
public interface DeleteUserUseCase {

    /**
     * Ejecuta la eliminación de un usuario del sistema mediante su identificador.
     *
     * @param id Identificador único del usuario a eliminar.
     */
    void deleteUser(UUID id);
}