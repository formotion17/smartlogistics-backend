package com.enterprise.user.infrastructure.config;

import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.input.DeleteUserUseCase;
import com.enterprise.user.application.ports.input.GetAllUsersUseCase;
import com.enterprise.user.application.ports.input.GetUserByIdUseCase;
import com.enterprise.user.application.ports.input.LoginUseCase;
import com.enterprise.user.application.ports.input.UpdateUserUseCase;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.application.usecase.CreateUserService;
import com.enterprise.user.application.usecase.DeleteUserService;
import com.enterprise.user.application.usecase.GetAllUsersService;
import com.enterprise.user.application.usecase.GetUserByIdService;
import com.enterprise.user.application.usecase.LoginService;
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
public CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort, PasswordEncoderPort passwordEncoderPort) {
    return new CreateUserService(userRepositoryPort, passwordEncoderPort);
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

     /**
     * Configura y registra el caso de uso de autenticación (Login) en el contexto de Spring.
     * <p>
     * Al instanciar {@link LoginService} aquí, mantenemos la capa de aplicación 
     * (nuestro caso de uso) completamente limpia y agnóstica al framework. Es decir, 
     * LoginService no necesita tener la anotación @Service de Spring.
     * </p>
     * * @param userRepositoryPort Puerto de salida para buscar al usuario por su email en la base de datos.
     * @param passwordEncoderPort Puerto de salida para comprobar matemáticamente que la contraseña plana coincide con el hash guardado.
     * @param tokenProviderPort Puerto de salida para fabricar el token JWT si el login es exitoso.
     * @return La implementación concreta (LoginService) disfrazada de su interfaz (LoginUseCase), 
     * lista para ser inyectada en el controlador REST.
     */
    @Bean
    public LoginUseCase loginUseCase(UserRepositoryPort userRepositoryPort, 
                                     PasswordEncoderPort passwordEncoderPort, 
                                     TokenProviderPort tokenProviderPort) {
        return new LoginService(userRepositoryPort, passwordEncoderPort, tokenProviderPort);
    }

    /**
     * Ensambla y registra la implementación GetAllUsersService como un Bean de Spring.
     * <p>
     * Esto es lo que permite que el controlador REST pueda inyectar la interfaz GetAllUsersUseCase.
     * Usamos esta clase de configuración en la infraestructura para mantener la capa de aplicación
     * totalmente limpia de anotaciones como @Service o @Component.
     * </p>
     *
     * @param userRepositoryPort El adaptador de repositorio de usuarios inyectado por Spring.
     * @return La implementación pura del caso de uso.
     */
    @Bean
    public GetAllUsersUseCase getAllUsersUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetAllUsersService(userRepositoryPort);
    }
}