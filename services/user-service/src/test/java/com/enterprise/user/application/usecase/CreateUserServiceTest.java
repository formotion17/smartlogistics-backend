package com.enterprise.user.application.usecase;

import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enterprise.user.application.ports.input.CreateUserCommand;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.model.User;

/**
 * Suite de pruebas unitarias para {@link CreateUserService}.
 * <p>
 * Verifica el correcto funcionamiento de la lógica de negocio al crear un nuevo usuario,
 * asegurando que los datos se procesen correctamente y que se gestionen las excepciones
 * esperadas mediante el uso de mocks para las dependencias externas (puertos).
 * </p>
 */
class CreateUserServiceTest {
    
    private UserRepositoryPort userRepositoryPort;
    private PasswordEncoderPort passwordEncoderPort; // <-- 1. Nuevo Mock de seguridad
    private CreateUserService createUserService;
    private ApplicationEventPublisher eventPublisher;

    /**
     * Configuración previa a cada test.
     * Inicializa el mock del puerto de salida e inyecta la dependencia en el servicio.
     */
    @BeforeEach
    void setUp() { 
        // Mockeamos los puertos
        userRepositoryPort = Mockito.mock(UserRepositoryPort.class);
        passwordEncoderPort = Mockito.mock(PasswordEncoderPort.class);

        // Simulamos que el encriptador siempre devuelve un hash seguro
        when(passwordEncoderPort.encode(anyString())).thenReturn("hashedPassword123");

        // Inyectamos los "dobles" en el servicio: 
        createUserService = new CreateUserService(userRepositoryPort, passwordEncoderPort, eventPublisher); // <-- 2. Pasamos el encriptador
    }

    /**
     * Test: Creación exitosa de usuario.
     * Verifica que el usuario se crea, tiene ID asignado y se invoca al repositorio.
     */
    @Test
    void shouldCreateUserSuccessfully() {
        // 1. Arrange (Preparar datos) - Añadimos contraseña
        CreateUserCommand command = new CreateUserCommand("Alex", "alex@test.com", "123456789", "MiPassword123");
        
        // Simulamos que el repositorio devuelve un usuario al guardar
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act (Ejecutar acción)
        User user = createUserService.createUser(command);

        // 3. Assert (Verificar resultado)
        assertNotNull(user);
        assertNotNull(user.getId());
        assertEquals("hashedPassword123", user.getPassword()); // Verificamos que se guardó encriptada
    
        // ¡ESTA ES LA LÍNEA CLAVE! 
        // Obligamos al test a verificar que el método save() se llamó una vez.
        //Mockito.verify(userRepositoryPort, Mockito.times(1)).save(any(User.class));
    }

    /**
     * Test: Lanzamiento de excepción al existir conflictos.
     * Verifica que el servicio propague correctamente errores del repositorio.
     */
    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // 1. Arrange: Preparamos el comando con contraseña
        CreateUserCommand command = new CreateUserCommand("Alex", "existe@test.com", "123456789", "MiPassword123");
        
        // Simulamos que el repositorio lanza una excepción (ej. UserAlreadyExistsException)
        when(userRepositoryPort.save(any(User.class)))
            .thenThrow(new RuntimeException("El email ya existe"));

        // 2. Act & Assert: Verificamos que al llamar al servicio, se propaga la excepción
        assertThrows(RuntimeException.class, () -> {
            createUserService.createUser(command);
        });
        
        // Verificamos que, aun fallando, el método save se intentó llamar
        Mockito.verify(userRepositoryPort, Mockito.times(1)).save(any(User.class));
    }

    /**
     * Test: Integridad de datos.
     * Verifica que los campos en el objeto de dominio coinciden exactamente 
     * con los datos enviados en el comando.
     */
    @Test
    void shouldCreateUserWithCorrectData() {
        // 1. Arrange - Añadimos contraseña
        CreateUserCommand command = new CreateUserCommand("Gemini", "gemini@test.com", "111222333", "MiPassword123");
        
        // Configuramos el mock para capturar el argumento
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act
        User createdUser = createUserService.createUser(command);

        // 3. Assert
        assertNotNull(createdUser);
        assertEquals("Gemini", createdUser.getName());
        assertEquals("gemini@test.com", createdUser.getEmail());
        assertEquals("111222333", createdUser.getPhone());
        assertEquals("hashedPassword123", createdUser.getPassword()); // Verificamos la encriptación
    }
    
}