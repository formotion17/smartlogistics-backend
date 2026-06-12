package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.UpdateUserCommand;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;

public class UpdateUserService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;

    public UpdateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        // 1. Buscar el usuario por ID
        User existingUser = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + command.id()));


        // 2. Actualizar los datos del usuario
        existingUser.setName(command.name());
        existingUser.setEmail(command.email());

        // 3. Guardar los cambios en el repositorio
        return userRepositoryPort.save(existingUser);
    }
    
}
