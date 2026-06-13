package com.enterprise.user.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;

/**
 * Caso de uso para la consulta de usuarios por su identificador único.
 * <p>
 * Implementa el puerto de entrada {@link GetUserByIdUseCase}. Su función principal
 * es delegar la recuperación de un usuario hacia la capa de infraestructura
 * mediante el puerto de salida {@link UserRepositoryPort}.
 * </p>
 */
public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;

    // Inyectamos el puerto de salida a través del constructor
    /**
     * Constructor para la inyección de dependencias del puerto de salida.
     * @param userRepositoryPort Puerto de salida que gestiona el acceso a datos.
     */
    public GetUserByIdService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Recupera un usuario basándose en su UUID.
     * @param id Identificador único del usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si existe, o un valor vacío.
     */
    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepositoryPort.findById(id);
    }
}
