package com.enterprise.user.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

// IMPORTS PARA CORS
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

import com.enterprise.user.infrastructure.config.security.CustomAccessDeniedHandler;
import com.enterprise.user.infrastructure.config.security.JwtAuthenticationEntryPoint;
import com.enterprise.user.infrastructure.config.security.JwtAuthenticationFilter;

/**
 * Configuración central de seguridad de Spring.
 * Actúa como el "portero" que decide qué peticiones HTTP pasan, cuáles no, 
 * y qué niveles de privilegios (roles) se requieren para cada recurso.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint jwtEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, JwtAuthenticationEntryPoint jwtEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthFilter = jwtAuthenticationFilter;
        this.jwtEntryPoint = jwtEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 1. Activamos CORS usando nuestro Bean personalizado
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // Desactivamos CSRF porque vamos a usar Tokens (nuestra API es Stateless)
            .csrf(csrf -> csrf.disable())
            
            // Configuramos el manejo de excepciones para usar nuestra respuesta personalizada (401)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtEntryPoint) // Maneja los 401 (No Autenticado)
                .accessDeniedHandler(accessDeniedHandler) // Maneja los 403 (Sin Permisos)
            )
            
            // Configuramos las reglas de autorización de las rutas
            .authorizeHttpRequests(auth -> auth

                // Lista Blanca Pública: Documentación, Swagger y Check de Salud Médico (Actuator)
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/health", "/error").permitAll()                
                // Permitimos que cualquiera pueda hacer Login o Registrarse de forma anónima
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                
                // <--- NUEVO LOCK: CONTROL DE ACCESO BASADO EN ROLES (RBAC) --->
                // ¿Por qué lo ponemos aquí?
                // Impedimos que usuarios comunes saboteen el sistema borrando registros.
                // Al usar .hasRole("ADMIN"), Spring Security exige de forma automática que la petición
                // contenga la autoridad "ROLE_ADMIN" provista por el JwtAuthenticationFilter.
                // REGLA CRÍTICA: Debe declararse antes de .anyRequest().authenticated()
                //.requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                
                // Cualquier otra petición (como buscar todos, buscar por id, actualizar) requerirá estar autenticado
                .anyRequest().authenticated()
            )
            // Le decimos a Spring que NO cree sesiones en servidor, somos 100% Stateless con JWT
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Ponemos a nuestro interceptor de JWT antes del portero por defecto de Spring
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }

    /**
     * Configuración de CORS (Cross-Origin Resource Sharing).
     * Permite que aplicaciones Frontend externas (ej: React en localhost:3000) consuman esta API de forma segura.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        return source;
    }
}