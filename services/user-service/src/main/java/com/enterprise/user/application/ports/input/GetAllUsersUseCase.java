package com.enterprise.user.application.ports.input;

import com.enterprise.user.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Puerto de entrada (Input Port) que define el caso de uso para listar todos los usuarios.
 * <p>
 * En la Arquitectura Hexagonal, esta interfaz representa la "frontera" o contrato de lo que nuestra
 * aplicación sabe hacer. El controlador REST llamará a esta interfaz sin importarle cómo está implementada por debajo.
 * </p>
 */
public interface GetAllUsersUseCase {

    /**
     * Ejecuta el caso de uso para obtener usuarios de forma paginada.
     *
     * @param pageable Configuración de la paginación solicitada por el cliente (frontend).
     * @return Página de usuarios encontrados.
     */
    Page<User> execute(Pageable pageable);
}