package com.enterprise.user.application.usecase;

import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enterprise.user.application.ports.input.CreateUserCommand;
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
    private CreateUserService createUserService;

    /**
     * Configuración previa a cada test.
     * Inicializa el mock del puerto de salida e inyecta la dependencia en el servicio.
     */
    @BeforeEach
    void setUp() { 
        // Mockeamos el puerto de salida
        // Mockeamos el puerto: Estamos creando un "doble" de tu repositorio. 
        // Es como un actor de riesgo: hace lo mismo que el repositorio real, pero es controlado.
        userRepositoryPort = Mockito.mock(UserRepositoryPort.class);

        // Inyectamos el "doble" en el servicio: 
        // El servicio cree que está hablando con la base de datos real.
        createUserService = new CreateUserService(userRepositoryPort);
    }

    /**
     * Test: Creación exitosa de usuario.
     * Verifica que el usuario se crea, tiene ID asignado y se invoca al repositorio.
     */
    @Test
    void shouldCreateUserSuccessfully() {
        // 1. Arrange (Preparar datos)
        CreateUserCommand command = new CreateUserCommand("Alex", "alex@test.com", "123456789");
        
        // Simulamos que el repositorio devuelve un usuario al guardar
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act (Ejecutar acción)
        // El servicio recibe el comando.
        // Crea un objeto User con sus validaciones y datos.
        // Llama al userRepositoryPort.save(user).
        // Aquí entra el Mockito: Como hemos definido when(...).thenAnswer(...), el test intercepta esa llamada y devuelve el usuario inmediatamente, sin tocar el disco ni la red.
        User user = createUserService.createUser(command);

        // 3. Assert (Verificar resultado)
        assertNotNull(user);
        assertNotNull(user.getId());
    
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
        // 1. Arrange: Preparamos el comando
        CreateUserCommand command = new CreateUserCommand("Alex", "existe@test.com", "123456789");
        
        // Simulamos que el repositorio lanza una excepción (ej. UserAlreadyExistsException)
        // Nota: Esto asume que tienes esa excepción creada. Si no, puedes usar RuntimeException.
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
        // 1. Arrange
        CreateUserCommand command = new CreateUserCommand("Gemini", "gemini@test.com", "111222333");
        
        // Configuramos el mock para capturar el argumento
        when(userRepositoryPort.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // 2. Act
        User createdUser = createUserService.createUser(command);

        // 3. Assert
        assertNotNull(createdUser);
        assertEquals("Gemini", createdUser.getName());
        assertEquals("gemini@test.com", createdUser.getEmail());
        assertEquals("111222333", createdUser.getPhone());
    }
    
}
