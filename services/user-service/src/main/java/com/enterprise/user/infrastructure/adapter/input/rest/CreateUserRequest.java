package com.enterprise.user.infrastructure.adapter.input.rest;

public record CreateUserRequest(
    String name,
    String email
) {}