package com.duocuc.equipment.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Equipment Service - TecnoFix")
                .description("Management of equipment items checked in for repair and their types")
                .version("1.0.0"));
    }
}
