package com.enterprise.user.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.domain.model.UserStatus;

public class CreateUserService implements CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public CreateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public User createUser(CreateUserCommand command) {

        // 1. Generar los datos de sistema que faltan
        UUID newID = UUID.randomUUID();
        UserStatus status = UserStatus.ACTIVE;
        LocalDateTime now = LocalDateTime.now();
        
        // 2. Crear el objeto de dominio
        User newUser = new User(
            newID,
            command.name(),
            command.email(),
            status,
            now,
            command.phone()
        );

        // 3. Guardar el usuario usando el puerto de salida y devolverlo
        return userRepositoryPort.save(newUser);
    }
}
