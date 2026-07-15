package com.duocuc.bff.controller;

import com.duocuc.bff.dto.AuthResponse;
import com.duocuc.bff.dto.LoginRequest;
import com.duocuc.bff.dto.RegisterRequest;
import com.duocuc.bff.services.AuthService;
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
 * El bff no tiene base de datos propia (solo hace de proxy hacia ms-auth), por lo que
 * no aplica una prueba de repositorio aquí.
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
