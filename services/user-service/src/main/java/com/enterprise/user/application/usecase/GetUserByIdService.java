package com.enterprise.user.application.usecase;

import java.util.Optional;
import java.util.UUID;

import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;

public class GetUserByIdService implements GetUserByIdUseCase {

    private final UserRepositoryPort userRepositoryPort;

    // Inyectamos el puerto de salida a través del constructor
    public GetUserByIdService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        return userRepositoryPort.findById(id);
    }
}
