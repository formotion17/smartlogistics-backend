package com.enterprise.user.infrastructure.adapter.output.notification;

import com.enterprise.user.domain.event.UserRegisteredEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UserNotificationListener {

    /**
     * Escucha el evento de registro de usuario.
     * Al usar @Async, Spring ejecuta este método en un hilo secundario independiente.
     * ¡La API responderá al instante al cliente sin esperar a que termine este método!
     */
    @Async 
    @EventListener
    public void handleUserRegistered(UserRegisteredEvent event) {
        // Simulamos la tarea lenta (ej: conectar con el servidor SMTP de correos)
        try {
            Thread.sleep(6000); // 3 segundos de espera simulados
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("🤖 [EVENTO ASÍNCRONO] Correo de bienvenida enviado a: " + event.email());
        System.out.println("🤖 [EVENTO ASÍNCRONO] Workspace inicial creado para el ID: " + event.id());
    }
}