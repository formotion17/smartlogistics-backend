package com.enterprise.user.application.ports.input;

import com.enterprise.user.domain.model.User;

public interface CreateUserUseCase {
    User createUser(CreateUserCommand command);
}