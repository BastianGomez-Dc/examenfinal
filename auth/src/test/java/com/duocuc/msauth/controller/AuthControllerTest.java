package com.duocuc.msauth.controller;

import com.duocuc.msauth.dto.AuthResponse;
import com.duocuc.msauth.dto.LoginRequest;
import com.duocuc.msauth.dto.RegisterRequest;
import com.duocuc.msauth.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Prueba unitaria del controller: se instancia directamente y se mockea AuthService.
 * A diferencia del resto de microservicios, AuthController retorna el DTO directo
 * (sin ResponseEntity), igual que en el repositorio de referencia del docente.
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    @DisplayName("login: retorna el token que entrega AuthService")
    void login_deberiaRetornarToken() {
        // Given
        LoginRequest request = new LoginRequest("user1@mail.cl", "abcd.1234");
        when(authService.login(request)).thenReturn(new AuthResponse("token-jwt"));

        // When
        AuthResponse response = authController.login(request);

        // Then
        assertEquals("token-jwt", response.token());
    }

    @Test
    @DisplayName("register: retorna el token que entrega AuthService")
    void register_deberiaRetornarToken() {
        // Given
        RegisterRequest request = new RegisterRequest("nuevo@mail.cl", "abcd.1234");
        when(authService.register(request)).thenReturn(new AuthResponse("token-nuevo"));

        // When
        AuthResponse response = authController.register(request);

        // Then
        assertEquals("token-nuevo", response.token());
    }
}
