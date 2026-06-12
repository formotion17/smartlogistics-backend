package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;

import java.util.UUID;

public class DeleteUserService implements DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public void deleteUser(UUID id) {
        // 1. Comprobamos si existe (si no, lanza la excepción y se corta la ejecución)
        userRepositoryPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException("No se puede borrar. El usuario con ID " + id + " no existe."));

        // 2. Si llegamos aquí, es que existe, así que lo borramos
        userRepositoryPort.deleteById(id);
    }
}