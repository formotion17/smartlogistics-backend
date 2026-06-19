package com.enterprise.user.application.usecase;

import com.enterprise.user.application.ports.input.LoginUseCase;
import com.enterprise.user.application.ports.output.PasswordEncoderPort;
import com.enterprise.user.application.ports.output.TokenProviderPort;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.domain.exception.UserNotFoundException;
import com.enterprise.user.domain.model.User;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.context.SecurityContextHolder;

public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenProviderPort tokenProviderPort;
    private final CacheManager cacheManager; // Inyectamos el gestor de caché

    public LoginService(UserRepositoryPort userRepositoryPort, 
                        PasswordEncoderPort passwordEncoderPort, 
                        TokenProviderPort tokenProviderPort,
                        CacheManager cacheManager) {
        this.userRepositoryPort = userRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenProviderPort = tokenProviderPort;
        this.cacheManager = cacheManager;
    }

    @Override
    public String login(String email, String password) {
        
        // 1. LIMPIEZA ATÓMICA: Eliminamos cualquier rastro antes de empezar
        SecurityContextHolder.clearContext();
        
        // Limpiamos la caché de Redis por email para forzar a que la siguiente 
        // lectura traiga el objeto fresco de la base de datos (con la password)
        if (cacheManager.getCache("usersByEmail") != null) {
            cacheManager.getCache("usersByEmail").evict(email);
        }

        // 2. Buscamos al usuario (ahora irá a DB y no a Redis)
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado"));

        // 3. Verificamos la contraseña
        if (!passwordEncoderPort.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 4. Auditoría y Token
        userRepositoryPort.registrarAccesoLogin(user);
        return tokenProviderPort.generateToken(user);
    }
}