package com.enterprise.user.application.ports.output;

import com.enterprise.user.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User save(User user);
    Optional<User> findByEmail(String email);
}
