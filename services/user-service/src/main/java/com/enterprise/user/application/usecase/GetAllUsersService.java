package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.GetAllUsersUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Implementación del caso de uso para obtener todos los usuarios de forma paginada.
 * <p>
 * Esta clase pertenece a la capa de Aplicación. Orquesta el flujo llamando al puerto de salida
 * (UserRepositoryPort) para recuperar los datos. Al no tener anotaciones de Spring (como @Service),
 * garantizamos que esta clase es "pura" y no depende de ningún framework externo.
 * </p>
 */
public class GetAllUsersService implements GetAllUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    /**
     * Constructor para inyectar el puerto de salida.
     *
     * @param userRepositoryPort Adaptador de base de datos disfrazado de puerto.
     */
    public GetAllUsersService(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    public Page<User> execute(Pageable pageable) {
        // En un futuro podríamos meter aquí lógica de negocio adicional (ej. filtrar usuarios inactivos).
        // En este caso, simplemente delegamos la búsqueda paginada al puerto de salida.
        return userRepositoryPort.findAll(pageable);
    }
}