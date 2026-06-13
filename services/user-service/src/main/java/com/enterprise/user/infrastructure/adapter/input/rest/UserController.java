package com.enterprise.user.infrastructure.adapter.input.rest;

import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.input.UpdateUserCommand;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase, UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {

        // 1. Mapear los datos de entreda a lo que entiende nuestro command de aplicación
        CreateUserCommand command = new CreateUserCommand(request.name(), request.email(), request.phone());

        // 2. Ejecutar el caso de uso
        User createdUser = createUserUseCase.createUser(command);

        // 3. Devolver la respuesta con el usuario creado y un codigo HTTP 201 Created
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // 1. Por URL con path variable
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserByPath(@PathVariable UUID id) {
        User user = getUserByIdUseCase.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("El usuario con ID " + id + " no existe."));
        return ResponseEntity.ok(user);
    }

    // 2. Por parametro (Query Param)
    @GetMapping
    public ResponseEntity<User> getUserByParam(@RequestParam UUID id) {
        User user = getUserByIdUseCase.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("El usuario con ID " + id + " no existe."));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand(id, request.name(), request.email(),request.phone());
        User updatedUser = updateUserUseCase.updateUser(command);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<java.util.Map<String, String>> deleteUser(@PathVariable UUID id) {
        // 1. Ejecuta el caso de uso (si no existe, saltará el 404 del ExceptionHandler)
        deleteUserUseCase.deleteUser(id);
        
        // 2. Si todo va bien, construye un mapa con el mensaje de éxito
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("mensaje", "Usuario borrado correctamente");
        
        // 3. Devuelve 200 OK con el cuerpo del JSON
        return ResponseEntity.ok(response);
    }
    
}
