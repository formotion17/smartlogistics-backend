package com.enterprise.user.infrastructure.config;

import com.enterprise.user.application.ports.input.CreateUserUseCase;
import com.enterprise.user.application.ports.output.UserRepositoryPort;
import com.enterprise.user.application.usecase.CreateUserService;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.enterprise.user.infrastructure.adapter.output.persistence.repository")
@EntityScan(basePackages = "com.enterprise.user.infrastructure.adapter.output.persistence.entity")
public class UseCaseConfig {

    @Bean
    public CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort) {
        // Aquí conectamos las piezas manualmente:
        return new CreateUserService(userRepositoryPort);
    }
}