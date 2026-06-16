package com.enterprise.user.infrastructure.config.security;

import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Interceptor HTTP que valida los tokens JWT en cada petición.
 * Extiende OncePerRequestFilter para garantizar que se ejecute una sola vez por petición.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProviderPort tokenProviderPort;
    private final UserRepositoryPort userRepositoryPort;

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
            // No trae token o el formato es incorrecto, le dejamos pasar sin autenticar.
            // Spring Security lo bloqueará más adelante si la ruta es privada.
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Leemos la pulsera (Extraemos el token quitando los 7 caracteres de "Bearer ")
        jwt = authHeader.substring(7);
        userEmail = tokenProviderPort.extractUsername(jwt); // Usamos nuestro puerto para leer el email

        // 3. Si la pulsera tiene un email y el usuario aún no ha sido autenticado en este hilo...
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            
            // Vamos a buscar al usuario a la BD (usando nuestro puerto hexagonal)
            User user = userRepositoryPort.findByEmail(userEmail).orElse(null);

            // 4. Validamos que el token pertenezca a ese usuario y no esté caducado
            if (user != null && tokenProviderPort.isTokenValid(jwt, user)) {
                
                // 5. ¡Todo correcto! Le decimos a Spring Security que este usuario está autenticado
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList() // Aquí irían los roles (ADMIN, USER) en el futuro
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // Guardamos el pase en el contexto de seguridad
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // 6. Fin de nuestro trabajo, pasamos la petición al siguiente eslabón (el Controlador)
        filterChain.doFilter(request, response);
    }
}