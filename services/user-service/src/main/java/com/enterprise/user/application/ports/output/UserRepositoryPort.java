package com.enterprise.user.application.ports.output;

import com.enterprise.user.domain.model.User;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    /**
     * Recupera una página de usuarios desde la base de datos.
     * <p>
     * Este método define el contrato que la capa de infraestructura (el adaptador de base de datos)
     * debe cumplir. Al devolver un objeto {@link Page}, nos aseguramos de no cargar todos los registros
     * en memoria de golpe, lo que es vital para el rendimiento cuando la tabla crece a miles de registros.
     * </p>
     *
     * @param pageable Objeto que contiene los parámetros de paginación (número de página, tamaño y orden).
     * @return Una "Página" (Page) que contiene la lista de usuarios correspondientes a los criterios solicitados y metadatos útiles.
     */
    Page<User> findAll(Pageable pageable);
}
