package com.enterprise.user.infrastructure.adapter.input.rest;

import com.enterprise.user.application.ports.input.LoginUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Adaptador de entrada REST para gestionar la autenticación.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;

    public AuthController(LoginUseCase loginUseCase) {
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest request) {
        // Llamamos al caso de uso.
        String token = loginUseCase.login(request.email(), request.password());
        
        // Devolvemos el Token JWT.
        return ResponseEntity.ok(token);
    }
}