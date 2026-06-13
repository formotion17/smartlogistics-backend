package com.enterprise.user.infrastructure.config;

import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.application.usecase.CreateUserService;
import com.enterprise.user.application.usecase.DeleteUserService;
import com.enterprise.user.application.usecase.GetUserByIdService;
import com.enterprise.user.application.usecase.UpdateUserService;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase de configuración para la inyección de dependencias de los casos de uso.
 * <p>
 * En la Arquitectura Hexagonal, esta clase actúa como el ensamblador (assembler) que
 * conecta los puertos de entrada (UseCases) con los adaptadores de salida (Infrastructure).
 * </p>
 * <p>
 * Configura las rutas para el escaneo de entidades JPA y repositorios de Spring Data,
 * asegurando que la capa de persistencia sea reconocida correctamente por el contenedor de Spring.
 * </p>
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.enterprise.user.infrastructure.adapter.output.persistence.repository")
@EntityScan(basePackages = "com.enterprise.user.infrastructure.adapter.output.persistence.entity")
public class UseCaseConfig {

    /**
     * Define el bean para el caso de uso de creación de usuarios.
     * @param userRepositoryPort El puerto de salida (interfaz) para persistencia.
     * @return Implementación concreta {@link CreateUserService}.
     */
    @Bean
    public CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort) {
        // Aquí conectamos las piezas manualmente:
        return new CreateUserService(userRepositoryPort);
    }

    /**
     * Define el bean para el caso de uso de consulta de usuario por ID.
     * @param userRepositoryPort El puerto de salida (interfaz) para persistencia.
     * @return Implementación concreta {@link GetUserByIdService}.
     */
    @Bean
    public GetUserByIdUseCase getUserByIdUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserByIdService(userRepositoryPort);
    }

    /**
     * Define el bean para el caso de uso de actualización de usuarios.
     * @param userRepositoryPort El puerto de salida (interfaz) para persistencia.
     * @return Implementación concreta {@link UpdateUserService}.
     */
    @Bean
    public UpdateUserUseCase updateUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new UpdateUserService(userRepositoryPort);
    }

    /**
     * Define el bean para el caso de uso de eliminación de usuarios.
     * @param userRepositoryPort El puerto de salida (interfaz) para persistencia.
     * @return Implementación concreta {@link DeleteUserService}.
     */
    @Bean
    public DeleteUserUseCase deleteUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new DeleteUserService(userRepositoryPort);
    }
}