package com.taskmanager.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Task Manager API")
                        .description("REST API for Task Management with JWT Auth and Redis")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Task Manager")
                                .email("admin@taskmanager.com")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Auth"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Auth",
                                new SecurityScheme()
                                        .name("Bearer Auth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Enter your JWT token")));
    }
}