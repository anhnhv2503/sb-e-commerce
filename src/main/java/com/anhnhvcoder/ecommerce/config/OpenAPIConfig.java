package com.anhnhvcoder.ecommerce.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "E-commerce API",
                version = "1.0",
                description = "REST API for the VA clothing shop. Protected endpoints need a JWT: "
                        + "call POST /api/auth/login, then click Authorize and paste the token.",
                contact = @Contact(
                        name = "VA Team",
                        email = "anhnhv2503@gmail.com"
                )
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local Development Server")
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "JWT Token Authentication",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenAPIConfig {

    @Bean
    public GroupedOpenApi allApi() {
        return GroupedOpenApi.builder()
                .group("1. all")
                .pathsToMatch("/api/**")
                .build();
    }

    @Bean
    public GroupedOpenApi shopApi() {
        return GroupedOpenApi.builder()
                .group("2. shop")
                .pathsToMatch("/api/**")
                .pathsToExclude("/api/admin/**", "/api/dashboard/**")
                .build();
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("3. admin")
                .pathsToMatch("/api/admin/**", "/api/dashboard/**")
                .build();
    }
}
