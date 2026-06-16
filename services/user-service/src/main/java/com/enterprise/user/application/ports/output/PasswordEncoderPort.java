package com.enterprise.user.application.ports.output;

/**
 * Puerto de salida para la encriptación de contraseñas.
 * Define el contrato que debe implementar la infraestructura 
 * para asegurar las contraseñas sin acoplar el dominio a librerías externas.
 */
public interface PasswordEncoderPort {
    
    /**
     * Encripta una contraseña en texto plano.
     */
    String encode(String plainPassword);
    
    /**
     * Compara una contraseña en texto plano con un hash ya encriptado.
     */
    boolean matches(String plainPassword, String encodedPassword);
}