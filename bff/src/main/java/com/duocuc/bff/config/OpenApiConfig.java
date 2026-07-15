package com.duocuc.bff.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("BFF - TecnoFix")
                .description("Backend for Frontend: expone login/registro delegando en ms-auth")
                .version("1.0.0"));
    }
}
