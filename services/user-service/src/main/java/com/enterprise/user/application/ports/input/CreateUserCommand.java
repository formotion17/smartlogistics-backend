package com.enterprise.user.application.ports.input;

public record CreateUserCommand(
    String name,
    String email
){}
