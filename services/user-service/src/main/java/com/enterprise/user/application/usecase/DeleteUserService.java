package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;

import java.util.UUID;

/**
 * Caso de uso para la eliminación de un usuario del sistema.
 * <p>
 * Implementa el puerto de entrada {@link DeleteUserUseCase}. Su responsabilidad es
 * coordinar la verificación previa a la eliminación y ejecutar la acción a través
 * del puerto de salida {@link UserRepositoryPort}.
 * </p>
 */
public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    /**
     * Constructor para inyección de dependencias.
     * @param userRepositoryPort Puerto de salida utilizado para acceder a los datos.
     */
    public DeleteUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Ejecuta la lógica de eliminación de un usuario.
     * <p>
     * Antes de eliminar, el servicio verifica la existencia del usuario. Si no existe,
     * se lanza una {@link UserNotFoundException}, permitiendo al controlador 
     * devolver un error 404 al cliente.
     * </p>
     * * @param id Identificador único del usuario a eliminar.
     * @throws UserNotFoundException si el usuario no existe en la base de datos.
     */
    @Override
    public void deleteUser(UUID id) {
        // 1. Comprobamos si existe (si no, lanza la excepción y se corta la ejecución)
        userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se puede borrar. El usuario con ID " + id + " no existe."));

        // 2. Si llegamos aquí, es que existe, así que lo borramos
        userRepositoryPort.deleteById(id);
    }
}