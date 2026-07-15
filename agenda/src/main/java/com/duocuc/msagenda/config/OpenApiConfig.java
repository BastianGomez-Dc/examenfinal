package com.duocuc.msagenda.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI().info(new Info()
                .title("MS Agenda - TecnoFix")
                .description("Microservicio de agendamiento de citas entre clientes y técnicos")
                .version("1.0.0"));
    }
}
