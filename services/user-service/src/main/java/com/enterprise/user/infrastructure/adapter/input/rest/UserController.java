package com.enterprise.user.infrastructure.adapter.input.rest;

import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.infrastructure.config.security.IsAdmin;

import jakarta.validation.Valid;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.input.GetAllUsersUseCase;
import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.input.UpdateUserCommand;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Adaptador de entrada REST para la gestión de usuarios.
 * <p>
 * Este controlador expone los endpoints de la API y delega la ejecución de las 
 * operaciones a los casos de uso (puertos de entrada). Mantiene la lógica de 
 * negocio aislada del protocolo HTTP.
 * </p>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;
    /**
     * Constructor para inyección de dependencias de los casos de uso.
     */
    public UserController(CreateUserUseCase createUserUseCase, GetUserByIdUseCase getUserByIdUseCase,
        UpdateUserUseCase updateUserUseCase, DeleteUserUseCase deleteUserUseCase, GetAllUsersUseCase getAllUsersUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.getAllUsersUseCase = getAllUsersUseCase;
    }

    /**
     * Endpoint para crear un nuevo usuario.
     * @param request Datos de entrada validados del cuerpo de la petición.
     * @return Usuario creado con código de estado 201 Created.
     */
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest request) {

        // 1. Mapear los datos de entreda a lo que entiende nuestro command de aplicación
        CreateUserCommand command = new CreateUserCommand(request.name(), request.email(), request.phone(),request.password());

        // 2. Ejecutar el caso de uso
        User createdUser = createUserUseCase.createUser(command);

        // 3. Devolver la respuesta con el usuario creado y un codigo HTTP 201 Created
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // 1. Por URL con path variable
    /**
     * Obtiene un usuario por su identificador único.
     * * 💡 PERLITA DE SEGURIDAD: Control de acceso dinámico.
     * #id hace referencia al parámetro @PathVariable UUID id del método.
     * authentication.principal.id accede automáticamente al método getId() del objeto User 
     * de dominio que tu filtro inyectó en la sesión.
     * @param id Identificador único del usuario.
     * @return El usuario encontrado con código 200 OK.
     * @throws UserNotFoundException Si el usuario no existe.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> getUserByPath(@PathVariable UUID id) {
        User user = getUserByIdUseCase.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("El usuario con ID " + id + " no existe."));
        return ResponseEntity.ok(user);
    }

    // 2. Por parametro (Query Param)
    /**
     * Busca un usuario por ID mediante parámetro de consulta (Query Param).
     * @param id Identificador único del usuario.
     * @return El usuario encontrado con código 200 OK.
    
    @GetMapping
    public ResponseEntity<User> getUserByParam(@RequestParam UUID id) {
        User user = getUserByIdUseCase.getUserById(id)
                .orElseThrow(() -> new UserNotFoundException("El usuario con ID " + id + " no existe."));
        return ResponseEntity.ok(user);
    } */

    /**
     * Actualiza un usuario existente.
     * @param id Identificador único del usuario.
     * @param request Datos actualizados del usuario.
     * @return Usuario actualizado con código 200 OK.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<User> updateUser(@PathVariable UUID id, @Valid @RequestBody UpdateUserRequest request) {
        UpdateUserCommand command = new UpdateUserCommand(id, request.name(), request.email(),request.phone(),request.password(),request.status());
        User updatedUser = updateUserUseCase.updateUser(command);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Elimina un usuario por su ID.
     * @param id Identificador único del usuario.
     * @return Respuesta con mensaje de éxito y código 200 OK.
     */
    @DeleteMapping("/{id}")
    @IsAdmin
    public ResponseEntity<java.util.Map<String, String>> deleteUser(@PathVariable UUID id) {
        // 1. Ejecuta el caso de uso (si no existe, saltará el 404 del ExceptionHandler)
        deleteUserUseCase.deleteUser(id);
        
        // 2. Si todo va bien, construye un mapa con el mensaje de éxito
        java.util.Map<String, String> response = new java.util.HashMap<>();
        response.put("mensaje", "Usuario borrado correctamente");
        
        // 3. Devuelve 200 OK con el cuerpo del JSON
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para obtener una lista paginada de todos los usuarios del sistema.
     * <p>
     * Permite a los clientes (ej. un frontend web) recuperar usuarios por bloques en lugar de descargar miles de golpe.
     * Spring convierte automáticamente los parámetros de la URL (ej: /api/users?page=0&size=5&sort=name,asc)
     * en el objeto {@link Pageable}.
     * </p>
     * <p>
     * 💡 PERLITA ARQUITECTÓNICA: Retorna un {@link PagedResponse} agnóstico para evitar la fuga de
     * abstracciones de Spring Data (Page) hacia el exterior de la aplicación, blindando el contrato de la API.
     * </p>
     *
     * @param pageable Parámetros de paginación interceptados por Spring.
     * Si el cliente no envía parámetros en la URL, se usarán los valores por defecto 
     * (@PageableDefault): página 0, tamaño 10, ordenados por email.
     * @return Una respuesta HTTP 200 (OK) con el JSON de estructura corporativa limpia (PagedResponse) 
     * que contiene la lista filtrada de usuarios y los metadatos esenciales requeridos por el Frontend.
     */
    @GetMapping
    @IsAdmin
    public ResponseEntity<PagedResponse<User>> getAllUsers(
            @PageableDefault(size = 10, page = 0, sort = "email") Pageable pageable) {
        
        // 1. Invocamos tu caso de uso de Dominio (sigue retornando un Page original)
        Page<User> usersPage = getAllUsersUseCase.execute(pageable);
        
        // 2. Transmutamos mágicamente el Page al nuevo DTO controlado e inmutable
        PagedResponse<User> response = PagedResponse.from(usersPage);
        
        // 3. Retornamos la respuesta limpia y desacoplada del framework
        return ResponseEntity.ok(response);
    }
    
    /**
     * Obtiene el perfil del usuario actualmente autenticado en la sesión.
     * <p>
     * ¡Magia de Spring Security! Al usar @AuthenticationPrincipal, el framework intercepta 
     * el token de la petición, extrae el objeto 'User' que guardó tu Filtro, y te lo inyecta 
     * directamente aquí ya mapeado. No necesitas hacer consultas extras a la base de datos.
     * </p>
     *
     * @param currentUser El usuario del dominio que ha iniciado sesión.
     * @return El perfil completo del usuario autenticado.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // 💡 ¡AQUÍ ESTÁ LA CLAVE! Exige estar autenticado, pero abre la puerta a todos los roles.
    public ResponseEntity<User> getMyProfile(@AuthenticationPrincipal User currentUser) {
        // Como el objeto ya contiene toda la información cargada desde el filtro, 
        // simplemente lo devolvemos con un 200 OK. ¡Limpio, elegante y eficiente!
        return ResponseEntity.ok(currentUser);
    }
}
