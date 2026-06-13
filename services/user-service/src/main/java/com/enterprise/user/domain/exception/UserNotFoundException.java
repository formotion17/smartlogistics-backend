package com.enterprise.user.domain.exception;

/**
 * Excepción lanzada cuando no se puede encontrar un usuario en el sistema.
 * <p>
 * Esta es una excepción de dominio (unchecked) que indica una situación de negocio 
 * no encontrada, permitiendo que el manejador global de excepciones 
 * ({@code GlobalExceptionHandler}) la capture y traduzca a un código de estado 
 * HTTP 404 (Not Found) de manera limpia y centralizada.
 * </p>
 */
public class UserNotFoundException extends RuntimeException {
    /**
     * Construye una nueva excepción con un mensaje específico.
     *
     * @param message Mensaje detallado que explica la razón por la que no se encontró el usuario.
     */
    public UserNotFoundException(String message) {
        super(message);
    }
}