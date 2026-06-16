package com.enterprise.user.infrastructure.config.jackson;

import com.enterprise.user.domain.model.User;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración centralizada de Jackson en la capa de Infraestructura.
 */
@Configuration
public class JacksonConfig {

    /**
     * Personalizador del serializador JSON. 
     * Vincula el modelo de dominio User con su MixIn correspondiente de infraestructura.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.mixIn(User.class, UserMixIn.class);
    }
}