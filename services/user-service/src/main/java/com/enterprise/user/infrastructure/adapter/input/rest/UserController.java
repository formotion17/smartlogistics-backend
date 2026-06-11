package com.enterprise.user.infrastructure.adapter.input.rest;

import com.enterprise.user.domain.model.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest request) {

        // 1. Mapear los datos de entreda a lo que entiende nuestro command de aplicación
        CreateUserCommand command = new CreateUserCommand(request.name(), request.email());

        // 2. Ejecutar el caso de uso
        User createdUser = createUserUseCase.createUser(command);

        // 3. Devolver la respuesta con el usuario creado y un codigo HTTP 201 Created
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }
        
    
}
