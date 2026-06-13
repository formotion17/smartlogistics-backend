package com.enterprise.user.application.ports.input;

import java.util.UUID;

public record UpdateUserCommand(
    UUID id,
    String name,
    String email,
    String phone
) {}