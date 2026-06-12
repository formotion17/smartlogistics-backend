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

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final CreateUserUseCase createUserUseCase;

    GlobalExceptionHandler(CreateUserUseCase createUserUseCase) {
        this.createUserUseCase = createUserUseCase;
    }

    // 1.Captura errores de validación del body (@Valid en POST)
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
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El parámetro '" + ex.getParameterName() + "' es obligatorio.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


    // 3. Captura cuando el formato del ID es incorrecto (ej: GET /api/users/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "El formato del ID es incorrecto. Se esperaba un UUID válido.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 4. <-- NUEVO: Captura rutas inexistentes o con barras '/' extra al final
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "La ruta solicitada no existe. Comprueba que la URL no termine en '/' o esté mal escrita.");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 5. <-- NUEVO: Captura excepciones de negocio (ej: usuario no encontrado)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUserNotFound(UserNotFoundException ex) {
        Map<String, String> error = new HashMap<>();
        // Extraemos el mensaje personalizado que le pasaremos desde el controlador
        error.put("error", ex.getMessage()); 
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
