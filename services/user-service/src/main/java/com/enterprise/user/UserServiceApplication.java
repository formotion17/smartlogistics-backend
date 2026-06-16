package com.enterprise.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


/**
 * Clase principal de inicio de la aplicación User Service.
 * * Esta clase actúa como el punto de entrada (Entry Point) del microservicio.
 * Su responsabilidad es configurar el contexto de Spring Boot, escanear los 
 * componentes de la arquitectura (puertos, adaptadores y casos de uso) e 
 * iniciar el servidor embebido.
 * * Al estar anotada con @SpringBootApplication, habilita las capacidades de:
 * <ul>
 * <li>Auto-configuración de Spring Boot.</li>
 * <li>Escaneo de componentes (Component Scanning) en el paquete base.</li>
 * <li>Configuración de beans basados en la infraestructura definida.</li>
 * </ul>
 */
@SpringBootApplication
@EnableAsync // 💡 ¡NUEVO! Permite ejecutar tareas en segundo plano
public class UserServiceApplication {

	/**
     * Método principal que ejecuta la aplicación Spring Boot.
     *
     * @param args Argumentos de la línea de comandos pasados al iniciar la aplicación.
     */
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
