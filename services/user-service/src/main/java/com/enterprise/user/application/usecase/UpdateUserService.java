package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.UpdateUserCommand;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;

/**
 * Caso de uso para la actualización de la información de un usuario.
 * <p>
 * Esta clase implementa el puerto de entrada {@link UpdateUserUseCase} y contiene 
 * la lógica de orquestación necesaria para recuperar, modificar y persistir 
 * un usuario existente en el sistema.
 * </p>
 */
public class UpdateUserService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;

    /**
     * Constructor para la inyección de la dependencia de persistencia (puerto de salida).
     * @param userRepositoryPort Puerto de salida utilizado para acceder a los datos del usuario.
     */
    public UpdateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Ejecuta la lógica de actualización de un usuario.
     * <p>
     * Recupera el usuario existente mediante el ID proporcionado en el comando. 
     * Si no se encuentra, lanza una excepción de dominio. Posteriormente, 
     * aplica los cambios y delega la persistencia al puerto de salida.
     * </p>
     * * @param command Comando que contiene los datos de actualización (ID, nombre, email, etc.).
     * @return El usuario actualizado.
     * @throws UserNotFoundException si el usuario con el ID proporcionado no existe.
     */
    @Override
    public User updateUser(UpdateUserCommand command) {
        // 1. Buscar el usuario por ID
        User existingUser = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + command.id()));


        // 2. Actualizar los datos del usuario
        existingUser.setName(command.name());
        existingUser.setEmail(command.email());
        existingUser.setPhone(command.phone());

        // 3. Guardar los cambios en el repositorio
        return userRepositoryPort.save(existingUser);
    }
    
}
