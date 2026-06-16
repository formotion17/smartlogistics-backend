package com.enterprise.user.infrastructure.adapter.output.security;

import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa el puerto de encriptación del dominio.
 * Utiliza BCrypt de Spring Security para generar y verificar hashes seguros.
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final BCryptPasswordEncoder passwordEncoder;

    public BCryptPasswordEncoderAdapter() {
        // Instanciamos la herramienta real de Spring Security
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @Override
    public String encode(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    @Override
    public boolean matches(String plainPassword, String encodedPassword) {
        return passwordEncoder.matches(plainPassword, encodedPassword);
    }
}