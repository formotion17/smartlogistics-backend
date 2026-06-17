package com.enterprise.user.infrastructure.config.security;

import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <--- NUEVO IMPORT
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List; // <--- NUEVO IMPORT

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Interceptor HTTP que valida los tokens JWT en cada petición.
 * Extiende OncePerRequestFilter para garantizar que se ejecute una sola vez por petición.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProviderPort;
    private final UserRepositoryPort userRepositoryPort;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtAuthenticationFilter(TokenProviderPort tokenProviderPort, UserRepositoryPort userRepositoryPort) {
        this.tokenProviderPort = tokenProviderPort;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // 1. ¿Trae pulsera? (Comprobamos si hay cabecera Authorization y si empieza por "Bearer ")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {

            // 2. Leemos la pulsera (Extraemos el token quitando los 7 caracteres de "Bearer ")
            jwt = authHeader.substring(7);
            userEmail = tokenProviderPort.extractUsername(jwt);

            // 3. Si la pulsera tiene un email y el usuario aún no ha sido autenticado en este hilo...
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Vamos a buscar al usuario a la BD (usando nuestro puerto hexagonal)
                User user = userRepositoryPort.findByEmail(userEmail).orElse(null);

                // 4. Validamos que el token pertenezca a ese usuario y no esté caducado
                if (user != null && tokenProviderPort.isTokenValid(jwt, user)) {
                    
                    // <--- NUEVO: MAPEO DE ROLES PARA SPRING SECURITY --->
                    // ¿Por qué lo hacemos así?
                    // Spring Security exige que los roles del sistema comiencen estrictamente por el prefijo "ROLE_".
                    // Tomamos el 'status' del usuario (ej: "ADMIN"), lo pasamos a mayúsculas y creamos la autoridad
                    // "ROLE_ADMIN". Esto es lo que activará el funcionamiento de '.hasRole("ADMIN")' en SecurityConfig.
                    String roleWithPrefix = "ROLE_" + user.getStatus().toString().toUpperCase();
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleWithPrefix);
                    List<SimpleGrantedAuthority> authorities = List.of(authority);

                    // 5. ¡Todo correcto! Le decimos a Spring Security que este usuario está autenticado y qué rol tiene
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            authorities // <--- ¡AQUÍ ESTÁ EL CAMBIO! Pasamos el rol real en vez de la lista vacía
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Guardamos el pase en el contexto de seguridad
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
            
            // 6. Fin de nuestro trabajo, pasamos la petición al siguiente eslabón (el Controlador)
            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            logger.warn("Acceso denegado: El token JWT ha expirado para la ruta: " + request.getRequestURI());
            convertirErrorEnJson(response, "El token de acceso ha caducado. Por favor, inicia sesión nuevamente.", HttpStatus.UNAUTHORIZED);
            
        } catch (JwtException e) {
            logger.warn("Acceso denegado: Token JWT inválido o malformado. Detalles: " + e.getMessage());
            convertirErrorEnJson(response, "Token JWT inválido, alterado o malformado.", HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Método auxiliar encargado de tomar el control del canal HTTP de salida (Response Stream)
     * para escribir un cuerpo JSON estructurado con el mismo estándar de tu GlobalExceptionHandler.
     */
    private void convertirErrorEnJson(HttpServletResponse response, String mensajeError, HttpStatus status) throws IOException {
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("timestamp", LocalDateTime.now().toString());
        errorBody.put("status", status.value());
        errorBody.put("error", status.getReasonPhrase());
        errorBody.put("message", mensajeError);

        response.getWriter().write(objectMapper.writeValueAsString(errorBody));
    }
}