package com.enterprise.user.application.ports.input;

import java.util.Optional;
import java.util.UUID;

import com.enterprise.user.domain.model.User;

/**
 * Puerto de entrada (Input Port) para la consulta de usuarios por su ID.
 * <p>
 * Esta interfaz define la operación necesaria para recuperar un usuario,
 * desacoplando la capa de entrada (Adaptadores) de la implementación del caso de uso.
 * </p>
 */
public interface GetUserByIdUseCase {

    /**
     * Recupera un usuario basándose en su identificador único.
     *
     * @param id Identificador único del usuario.
     * @return Un {@link Optional} que contiene el usuario si existe, o vacío en caso contrario.
     */
    Optional<User> getUserById(UUID id);
}
