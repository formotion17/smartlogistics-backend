package com.enterprise.user.infrastructure.config.jackson;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * MixIn de Infraestructura para el modelo de Dominio User.
 * <p>
 * Funciona como una clase espejo. Jackson leerá las anotaciones de aquí 
 * y las aplicará dinámicamente sobre la clase User del dominio, manteniendo
 * el dominio 100% libre de dependencias de librerías web.
 * </p>
 */
public interface UserMixIn {

    // Le indicamos a Jackson que ignore por completo este campo al serializar a JSON
    @JsonIgnore
    String getPassword();
}