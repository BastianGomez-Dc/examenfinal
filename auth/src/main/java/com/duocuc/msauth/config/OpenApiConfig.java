package com.duocuc.msauth.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("MS Auth - TecnoFix")
                .description("Microservicio de autenticación: login, registro y emisión de JWT")
                .version("1.0.0"));
    }
}
