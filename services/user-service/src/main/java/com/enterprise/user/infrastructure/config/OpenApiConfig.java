package com.enterprise.user.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para añadir soporte de JWT.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "Keycloak-OAuth2";
        
        return new OpenAPI()
                .info(new Info()
                        .title("User Service API - Hexagonal Architecture")
                        .version("v3.0")
                        .description("Ecosistema de Microservicios protegido con Seguridad Corporativa Keycloak."))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("Flujo de autenticación corporativa con Keycloak")
                                .flows(new io.swagger.v3.oas.models.security.OAuthFlows()
                                        .password(new io.swagger.v3.oas.models.security.OAuthFlow()
                                                // URL exacta de tu endpoint de tokens de Keycloak
                                                .tokenUrl("http://localhost:9090/auth/realms/smartlogistics-realm/protocol/openid-connect/token")
                                        )
                                )
                        ));
    }
}