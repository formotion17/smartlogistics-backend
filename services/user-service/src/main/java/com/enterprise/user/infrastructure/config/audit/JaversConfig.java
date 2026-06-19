package com.enterprise.user.infrastructure.config.audit;

import org.javers.spring.boot.sql.JaversSqlAutoConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JaversConfig {
    // Javers se autoconfigura, pero aquí podrías personalizar 
    // si quieres ignorar campos específicos como el ID o fechas de auditoría.
}