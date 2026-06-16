package com.enterprise.user.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.event.UserRegisteredEvent;
import com.enterprise.user.domain.model.User;
import com.enterprise.user.domain.model.UserStatus;

/**
 * Caso de uso: Creación de usuarios.
 * Esta clase orquesta la lógica de negocio necesaria para persistir un nuevo usuario.
 * Siguiendo arquitectura hexagonal, esta capa de aplicación no conoce detalles de infraestructura.
 */
public class CreateUserService implements CreateUserUseCase {

    // Puerto de salida: La interfaz que permite al dominio comunicarse con la persistencia.
    private final UserRepositoryPort userRepositoryPort;
    // Puerto de salida: La interfaz que permite al dominio comunicarse con la codificación de contraseñas.
    private final PasswordEncoderPort passwordEncoderPort;
    // Publicador nativo de eventos de Spring.
    private final ApplicationEventPublisher eventPublisher; // 💡 1. Inyectamos el publicador

    /**
     * Inyección de dependencias por constructor. 
     * Favorece la inmutabilidad y facilita los tests unitarios.
     */
    public CreateUserService(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort, ApplicationEventPublisher eventPublisher) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Ejecuta el proceso de creación de un usuario.
     * * @param command Contiene los datos necesarios provenientes de la capa de entrada (REST).
     * @return El usuario creado tras ser procesado por el dominio y guardado.
     */
    @Override
    public User createUser(CreateUserCommand command) {

        // 1. Enriquecimiento del modelo: Generamos datos internos que el usuario no envía.
        UUID newID = UUID.randomUUID();
        // ⚠️ CORRECCIÓN: Cambiamos 'ACTIVE' por 'USER' para que compile limpio con tu Enum real
        UserStatus status = UserStatus.USER; 
        LocalDateTime now = LocalDateTime.now();

        // 2. Codificación de la contraseña: Aseguramos que la contraseña no se almacene en texto plano.
        String encodedPassword = passwordEncoderPort.encode(command.password());
        
        // 3. Mapeo al dominio: Convertimos el comando en un objeto de dominio puro.
        User newUser = new User(
            newID,
            command.name(),
            command.email(),
            status,
            now,
            command.phone(),
            encodedPassword
        );

        // 4. Persistencia: Guardamos el usuario en la BD y capturamos la entidad devuelta
        User savedUser = userRepositoryPort.save(newUser);

        // 💡 5. ¡EL GRITO AL AIRE! Disparamos el evento de dominio de forma agnóstica.
        // Se ejecuta justo después de confirmar que el usuario se guardó bien en PostgreSQL.
        eventPublisher.publishEvent(new UserRegisteredEvent(
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail()
        ));

        // 6. Retornamos el usuario guardado con éxito.
        return savedUser;
    }
}
