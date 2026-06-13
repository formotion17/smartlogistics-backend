package com.enterprise.user.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
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

    /**
     * Inyección de dependencias por constructor. 
     * Favorece la inmutabilidad y facilita los tests unitarios.
     */
    public CreateUserService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    /**
     * Ejecuta el proceso de creación de un usuario.
     * * @param command Contiene los datos necesarios provenientes de la capa de entrada (REST).
     * @return El usuario creado tras ser procesado por el dominio y guardado.
     */
    @Override
    public User createUser(CreateUserCommand command) {

        // 1. Enriquecimiento del modelo: Generamos datos internos que el usuario no envía.
        // La capa de aplicación es responsable de preparar el modelo para el dominio.
        UUID newID = UUID.randomUUID();
        UserStatus status = UserStatus.ACTIVE;
        LocalDateTime now = LocalDateTime.now();
        
        // 2. Mapeo al dominio: Convertimos el comando en un objeto de dominio puro.
        // El modelo 'User' es el corazón de la aplicación y garantiza la consistencia de los datos.
        User newUser = new User(
            newID,
            command.name(),
            command.email(),
            status,
            now,
            command.phone()
        );

        // 3. Persistencia: Invocamos el puerto de salida.
        // Aquí es donde realmente se guarda el usuario en la BD.
        // Al usar un puerto, el servicio no sabe si estamos usando PostgreSQL, MongoDB o memoria.
        return userRepositoryPort.save(newUser);

        // Sabotaje para ver si el test funciona sin tocar la base de datos
        //return newUser; // Devolvemos el usuario creado sin guardarlo en la base de datos
    }
}
