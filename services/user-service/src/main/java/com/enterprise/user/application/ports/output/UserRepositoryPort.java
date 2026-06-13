package com.enterprise.user.application.ports.output;

import com.enterprise.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;

/**
 * Puerto de salida (Output Port) para la persistencia de usuarios.
 * <p>
 * Define las operaciones que la capa de aplicación requiere para interactuar con 
 * el sistema de almacenamiento. Este puerto permite desacoplar la lógica de 
 * negocio de la tecnología de persistencia utilizada.
 * </p>
 */
public interface UserRepositoryPort {
    
    /**
     * Guarda un usuario en el sistema de persistencia.
     * @param user El usuario a persistir.
     * @return El usuario guardado.
     */
    User save(User user);

    /**
     * Busca un usuario por su dirección de correo electrónico.
     * @param email Correo electrónico a buscar.
     * @return Un {@link Optional} con el usuario si existe, o vacío.
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca un usuario por su identificador único.
     * @param id UUID del usuario.
     * @return Un {@link Optional} con el usuario si existe, o vacío.
     */
    Optional<User> findById(UUID id);

    /**
     * Elimina un usuario del sistema basándose en su ID.
     * @param id UUID del usuario a eliminar.
     */
    void deleteById(UUID id);
}
