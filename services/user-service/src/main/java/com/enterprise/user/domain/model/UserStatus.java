package com.enterprise.user.domain.model;

/**
 * Representa los estados posibles en los que puede encontrarse un usuario dentro del sistema.
 * <p>
 * Al formar parte de la capa de Dominio, esta enumeración define una regla de negocio
 * core que debe ser respetada por cualquier adaptador (entrada o salida) que
 * interactúe con los usuarios.
 * </p>
 */
public enum UserStatus {
    ACTIVE,
    INACTIVE,
    ADMIN,
    DEV,
    USER
}
