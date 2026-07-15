package com.duocuc.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("Order Service - TecnoFix")
                .description("Core microservice for work orders: coordinates client, equipment, technician and services")
                .version("1.0.0"));
    }
}
