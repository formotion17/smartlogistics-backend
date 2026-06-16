package com.enterprise.user.infrastructure.config.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Clase encargada de capturar los intentos de acceso no autorizados (sin token o token inválido)
 * y devolver una respuesta JSON estructurada y dejar un log en la consola.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // Instanciamos el Logger para imprimir en consola
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) 
            throws IOException, ServletException {
        
        // 1. Dejar rastro en los Logs del servidor (Lo que verás en tu consola)
        logger.warn("⚠️ Intento de acceso bloqueado. Petición no autorizada a la ruta: {}", request.getRequestURI());

        // 2. Construir la respuesta JSON para el cliente (Lo que verás en Postman)
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // Código 401
        
        // Escribimos el JSON a mano directamente en la respuesta
        response.getWriter().write(
            "{\n" +
            "  \"status\": 401,\n" +
            "  \"error\": \"Unauthorized\",\n" +
            "  \"message\": \"Acceso denegado. No tienes un Token válido para acceder a este recurso.\",\n" +
            "  \"path\": \"" + request.getRequestURI() + "\"\n" +
            "}"
        );
    }
}