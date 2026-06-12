package com.enterprise.user.application.ports.input;

import com.enterprise.user.domain.model.User;

public interface UpdateUserUseCase {
    // Recibe el comando con los nuevos datos y deuelve el usuario actualizado
    User updateUser(UpdateUserCommand command);
}
