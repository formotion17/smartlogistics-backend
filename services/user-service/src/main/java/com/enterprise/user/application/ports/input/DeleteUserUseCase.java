package com.enterprise.user.application.ports.input;

import java.util.UUID;

public interface DeleteUserUseCase {
    void deleteUser(UUID id);
}