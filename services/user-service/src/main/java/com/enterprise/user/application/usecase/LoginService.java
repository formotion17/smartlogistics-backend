package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.LoginUseCase;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;

/**
 * Caso de uso: Login de usuarios.
 * Orquesta la verificación de credenciales y la emisión de tokens.
 */
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;

    public LoginService(UserRepositoryPort userRepositoryPort, 
                        PasswordEncoderPort passwordEncoderPort, 
                        TokenProviderPort tokenProviderPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
    }

    @Override
    public String login(String email, String password) {
        // 1. Buscamos al usuario
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // 2. Verificamos la contraseña
        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 3. Generamos el token
        return tokenProviderPort.generateToken(user);
    }
}