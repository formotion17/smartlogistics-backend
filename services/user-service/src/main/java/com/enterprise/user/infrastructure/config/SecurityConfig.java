package com.enterprise.user.infrastructure.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.enterprise.user.infrastructure.config.security.JwtAuthenticationEntryPoint;
import com.enterprise.user.infrastructure.config.security.JwtAuthenticationFilter;

/**
 * Configuración central de seguridad de Spring.
 * Actúa como el "portero" que decide qué peticiones HTTP pasan y cuáles no.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthenticationEntryPoint jwtEntryPoint) {
        this.jwtAuthFilter = jwtAuthenticationFilter;
        this.jwtEntryPoint = jwtEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http

            // Activamos CORS llamando al Bean que hemos creado abajo
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Desactivamos CSRF porque vamos a usar Tokens (nuestra API es Stateless)
            .csrf(csrf -> csrf.disable())
            
            // <--- NUEVO: Configuramos el manejo de excepciones para usar nuestra respuesta personalizada
            .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtEntryPoint))
            
            // Configuramos las reglas de autorización de las rutas
            .authorizeHttpRequests(auth -> auth

                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html","/actuator/health").permitAll()

                // 1. Permitimos que cualquiera pueda hacer Login
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                
                // 2. Permitimos que cualquiera pueda Registrarse
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                
                // 3. Cualquier otra petición (como buscar, borrar, etc.) requerirá estar autenticado
                .anyRequest().authenticated()
            )// <--- 2. Le decimos a Spring que NO cree sesiones (guarde cookies), somos 100% Stateless con JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // <--- 3. Ponemos a nuestro portero ANTES del portero por defecto de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            

        return http.build();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     * <p>
     * ¿Por qué lo ponemos? Por defecto, los navegadores web bloquean cualquier petición HTTP 
     * que venga de un dominio diferente al del servidor (ej. React en localhost:3000 pidiendo 
     * datos a Spring en localhost:8081). 
     * Con esta configuración creamos una "lista blanca" para permitir que nuestra app web 
     * se comunique con nuestra API de forma segura.
     * </p>
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // En desarrollo permitimos localhost:3000 (React/Vue/Angular). En prod sería tu dominio real.
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); 
        // Permitimos los métodos HTTP que nuestra API usa
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Permitimos que el Frontend nos envíe el token JWT y contenido JSON en las cabeceras
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicamos estas reglas a todos los endpoints de nuestra API (/**)
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}