package com.enterprise.user.application.usecase;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.enterprise.user.application.ports.input.UpdateUserCommand;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;

/**
 * Caso de uso para la actualización de la información de un usuario.
 * <p>
 * Esta clase implementa el puerto de entrada {@link UpdateUserUseCase} y contiene 
 * la lógica de orquestación necesaria para recuperar, modificar y persistir 
 * un usuario existente en el sistema.
 * </p>
 */
public class UpdateUserService implements UpdateUserUseCase {
    
    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort; //Inyectamos el puerto para no guardar texto plano

    /**
     * Constructor para la inyección de la dependencia de persistencia (puerto de salida).
     * @param userRepositoryPort Puerto de salida utilizado para acceder a los datos del usuario.
     */
    public UpdateUserService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    /**
     * Ejecuta la lógica de actualización de un usuario.
     * <p>
     * Recupera el usuario existente mediante el ID proporcionado en el comando. 
     * Si no se encuentra, lanza una excepción de dominio. Posteriormente, 
     * aplica los cambios y delega la persistencia al puerto de salida.
     * </p>
     * * @param command Comando que contiene los datos de actualización (ID, nombre, email, etc.).
     * @return El usuario actualizado.
     * @throws UserNotFoundException si el usuario con el ID proporcionado no existe.
     */
    @Override
    public User updateUser(UpdateUserCommand command) {
        // 1. Buscar el usuario por ID
        User existingUser = userRepositoryPort.findById(command.id())
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + command.id()));


        // 🚀 CONTROL SELECTIVO DE MUTACIÓN:
        // Solo invocamos los setters del dominio si el cliente envió datos reales en el JSON.
        // Si vienen nulos o vacíos, mantenemos intacto el valor que ya tenía el usuario.

        if (command.name() != null && !command.name().isBlank()) {
            existingUser.setName(command.name());
        }

        if (command.email() != null && !command.email().isBlank()) {
            existingUser.setEmail(command.email());
        }

        if (command.phone() != null) {
            existingUser.setPhone(command.phone()); // Permite vaciar el teléfono si se envía "" o actualizarlo
        }

        // 4. PROTECCIÓN DE CREDENCIALES: Si cambia la contraseña, la pasamos por el puerto de Hash
        if (command.password() != null && !command.password().isBlank()) {
            // 🚀 Encriptamos en caliente usando el componente hexagonal de la Fase 1
            String passwordEncriptada = passwordEncoderPort.encode(command.password());
            existingUser.setPassword(passwordEncriptada);
        }

        if (command.status() != null) {
            
            // Extraemos los privilegios del operador humano que está ejecutando la petición en Postman
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            boolean isAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

            // Si intenta alterar el estatus pero no es administrador, disparamos un cortocircuito de seguridad
            if (!isAdmin) {
                throw new AccessDeniedException("Operación denegada: Solo los administradores pueden alterar el estatus o rol de un usuario.");
            }

            // Si pasó el escudo, asignamos el enum directo sin necesidad de transformaciones de texto
            existingUser.setStatus(command.status());
        }

        // 2. Enviamos el dominio perfectamente válido y mezclado al puerto para persistir y desalojar cachés
        return userRepositoryPort.save(existingUser);
    }
    
}
