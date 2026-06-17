package com.enterprise.user.infrastructure.config;

import com.enterprise.user.domain.model.User;
import com.enterprise.user.infrastructure.config.jackson.UserMixIn; //IMPORTANTE: Importamos tu MixIn de la Fase 1
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule; //IMPORTANTE: Módulo para leer constructores con argumentos
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * <h2>CacheConfig - Configuración Centralizada del Sistema de Caché con Redis</h2>
 * <p>
 * Esta clase habilita la abstracción de caché de Spring (Spring Cache) y configura
 * a Redis como el proveedor de almacenamiento en memoria RAM (Cache Provider).
 * </p>
 * <p>
 * <b>Modificación Fase 2 (Fix Deserialización):</b> Se registra explícitamente el soporte 
 * para {@link UserMixIn} y {@link ParameterNamesModule} garantizando que Jackson pueda reconstruir 
 * modelos inmutables de dominio sin constructores vacíos desde la memoria RAM.
 * </p>
 *
 * @author Arquitecto de Software
 * @version 2.1
 * @since Fase 2 - Ecosistema Avanzado
 */
@Configuration
@EnableCaching // Activa interceptores en Spring para buscar @Cacheable en los métodos
public class CacheConfig {

    /**
     * Construye y registra el gestor de caché de Redis (CacheManager) adaptado a estándares empresariales.
     *
     * @param connectionFactory Fábrica de conexiones de Redis (Lettuce), provista automáticamente por Spring Boot.
     * @return El {@link RedisCacheManager} que controlará el almacenamiento de datos en la RAM.
     */
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        
        // 1. Instanciamos un ObjectMapper independiente y exclusivo para Redis.
        ObjectMapper redisObjectMapper = new ObjectMapper();
        
        // 2. Registramos el soporte para los nuevos tipos de fecha de Java 8 (LocalDateTime, Instant, etc.)
        redisObjectMapper.registerModule(new JavaTimeModule());
        
        // 🚀 SOLUCIÓN AL ERROR DE CONSTRUCTOR: Registramos el módulo de nombres de parámetros de Java.
        // Esto permite a Jackson inspeccionar los nombres reales de las variables del constructor de tu modelo User.
        redisObjectMapper.registerModule(new ParameterNamesModule());

        // 🚀 SOLUCIÓN AL ERROR DE CONSTRUCTOR: Vinculamos tu MixIn de la Fase 1 al ObjectMapper de Redis.
        // Le ordenamos a Jackson: "Cuando intentes leer o escribir la clase User.class en Redis, 
        // aplica estrictamente las reglas de construcción que definimos en UserMixIn.class".
        redisObjectMapper.addMixIn(User.class, UserMixIn.class);
        
        // 3. Activamos la inclusión de metadatos de tipo (Polimorfismo) para evitar ClassCastException
        redisObjectMapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance, 
                ObjectMapper.DefaultTyping.NON_FINAL, 
                JsonTypeInfo.As.PROPERTY
        );

        // 4. Creamos el serializador basado en el ObjectMapper que acabamos de calibrar.
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        // 5. Definimos el plano de configuración (Blueprint) por defecto para todas las regiones de caché.
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                // Establecemos un tiempo de vida (TTL) global de 60 minutos.
                .entryTtl(Duration.ofMinutes(60))
                // Evitamos almacenar valores nulos ("null") en la caché.
                .disableCachingNullValues()
                // Las claves se guardarán siempre en texto plano limpio (ej: usersById::UUID)
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // Los valores se guardarán en el JSON polimórfico estructurado con soporte de MixIns
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer));

        // 6. Construimos el gestor definitivo asociándolo a las conexiones del contenedor de Docker.
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .build();
    }
}