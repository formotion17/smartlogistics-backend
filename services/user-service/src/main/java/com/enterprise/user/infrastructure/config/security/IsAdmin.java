package com.enterprise.user.infrastructure.config.security;

import org.springframework.security.access.prepost.PreAuthorize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación personalizada de seguridad para restringir accesos 
 * únicamente a usuarios con privilegios de Administrador.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN')") // Encapsulamos la lógica aquí
public @interface IsAdmin {
}