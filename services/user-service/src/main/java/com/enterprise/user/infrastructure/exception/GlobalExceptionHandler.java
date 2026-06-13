package com.enterprise.user.infrastructure.exception;

import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.domain.exception.UserNotFoundException;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Manejador global de excepciones para la capa de infraestructura.
 * <p>
 * Utiliza {@link RestControllerAdvice} para interceptar excepciones lanzadas 
 * en cualquier controlador y transformarlas en respuestas HTTP coherentes. 
 * Esto desacopla la lógica de negocio de la gestión de errores de la API.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final CreateUserUseCase createUserUseCase;

    /**
     * Constructor para inyección de dependencias.
     * * @param createUserUseCase Caso de uso para la creación de usuarios (inyectado por Spring).
     */
    GlobalExceptionHandler(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    // 1.Captura errores de validación del body (@Valid en POST)
    /**
     * Maneja errores de validación en los objetos de entrada (@Valid).
     * * @param ex Excepción lanzada cuando la validación de un argumento falla.
     * @return ResponseEntity con un mapa de errores por campo y código de estado 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Extraemos cada error de validación y lo metemos en una mapa (createUserUseCase)
        ex.getBindingResult().getAllErrors().forEach((error) -> {       
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    
    }

    // 2. Captura cuando falta un parámetro en la URL (ej: GET /api/users sin el id)
    /**
     * Maneja errores cuando faltan parámetros obligatorios en las peticiones web.
     * * @param ex Excepción por falta de parámetros.
     * @return ResponseEntity con el mensaje de error y código de estado 400 Bad Request.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El parámetro '" + ex.getParameterName() + "' es obligatorio.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    // 3. Captura cuando el formato del ID es incorrecto (ej: GET /api/users/abc)
    /**
     * Maneja errores de tipado en los argumentos de los controladores.
     * * @param ex Excepción por tipo de dato incompatible.
     * @return ResponseEntity con mensaje informativo y código de estado 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El formato del ID es incorrecto. Se esperaba un UUID válido.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 4. <-- NUEVO: Captura rutas inexistentes o con barras '/' extra al final
    /**
     * Maneja rutas que no han podido ser resueltas por el DispatcherServlet.
     * * @param ex Excepción de recurso no encontrado.
     * @return ResponseEntity con mensaje de error y código de estado 404 Not Found.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "La ruta solicitada no existe. Comprueba que la URL no termine en '/' o esté mal escrita.");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 5. <-- NUEVO: Captura excepciones de negocio (ej: usuario no encontrado)
    /**
     * Maneja excepciones específicas del dominio (negocio).
     * * @param ex Excepción de tipo {@link UserNotFoundException}.
     * @return ResponseEntity con el mensaje de negocio y código de estado 404 Not Found.
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        // Extraemos el mensaje personalizado que le pasaremos desde el controlador
        error.put("error", ex.getMessage()); 
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
