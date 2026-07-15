package com.duocuc.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * El api-gateway no tiene lógica de negocio propia (solo enrutamiento declarado en
 * application.yml), por lo que su única prueba relevante es que el contexto cargue
 * correctamente con todas las rutas configuradas.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}
