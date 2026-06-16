package com.enterprise.user.infrastructure.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador personalizado para capturar errores de acceso denegado (HTTP 403 Forbidden).
 * Se ejecuta cuando un usuario está autenticado pero NO tiene el rol requerido (ej: intentar borrar sin ser ADMIN).
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        // 1. Forzamos el código de estado 403 y decimos que devolvemos un JSON en formato UTF-8
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 2. Construimos la estructura del JSON con tu mensaje personalizado
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", HttpStatus.FORBIDDEN.value());
        body.put("error", "Forbidden");
        body.put("message", "El usuario no tiene permisos para realizar esta accion"); // 💡 Tu mensaje personalizado
        body.put("path", request.getRequestURI());

        // 3. Escribimos el JSON en el cuerpo de la respuesta
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}