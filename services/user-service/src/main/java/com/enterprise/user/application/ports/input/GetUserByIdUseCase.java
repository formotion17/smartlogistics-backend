package com.enterprise.user.application.ports.input;

import java.util.Optional;
import java.util.UUID;

import com.enterprise.user.domain.model.User;

public interface GetUserByIdUseCase {
    Optional<User> getUserById(UUID id);
}
