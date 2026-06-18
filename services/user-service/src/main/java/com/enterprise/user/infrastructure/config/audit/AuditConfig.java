package com.enterprise.user.infrastructure.config.audit;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * <h2>AuditConfig - Configuración Centralizada de Auditoría JPA</h2>
 * <p>
 * Esta clase activa el soporte de auditoría de Spring Data JPA y registra el proveedor
 * encargado de resolver la identidad del operador transaccional (Auditor Provider).
 * </p>
 *
 * <b>Mecánica de Integración:</b>
 * Intercepta de forma segura el contexto de seguridad perimetral JWT en cada hilo
 * de ejecución para inyectar dinámicamente el autor en los campos de auditoría.
 *
 * @author Arquitecto de Software
 * @version 2.0
 * @since Fase 2 - Ecosistema Avanzado
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider") // Activa la auditoría automática asociándola a nuestro Bean
public class AuditConfig {

    /**
     * Define el componente encargado de decirle a JPA quién está realizando la acción actual.
     * <p>
     * Se conecta con el {@link SecurityContextHolder} de Spring Security, extrayendo el
     * identificador principal (UUID) guardado por el filtro de autenticación JWT.
     * </p>
     *
     * @return Un {@link AuditorAware} resolviendo cadenas de texto (Strings).
     */
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            // 1. Capturamos la información de autenticación del hilo actual
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // 2. Control de seguridad: Si no hay autenticación válida, asumimos que es una acción del sistema
            if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() == null) {
                return Optional.of("SYSTEM"); // Caso típico: Creación de un usuario de forma pública (Registro)
            }

            Object principal = authentication.getPrincipal();

            // 3. Éxito: Extraemos el UUID inmutable de la entidad de dominio autenticada
            if (principal instanceof com.enterprise.user.domain.model.User) {
                UUID actorUuid = ((com.enterprise.user.domain.model.User) principal).getId();
                return Optional.of(actorUuid != null ? actorUuid.toString() : "SYSTEM");
            }

            return Optional.of(authentication.getName()); // Fallback estándar por si viaja otro principal
        };
    }
}