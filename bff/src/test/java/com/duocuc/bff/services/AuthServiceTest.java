package com.duocuc.bff.services;

import com.duocuc.bff.client.AuthClient;
import com.duocuc.bff.dto.AuthResponse;
import com.duocuc.bff.dto.LoginRequest;
import com.duocuc.bff.dto.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuthService del BFF es un simple proxy hacia AuthClient (que llama a ms-auth).
 * Se mockea AuthClient porque representa la comunicación remota.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthClient authClient;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("login: delega en AuthClient y retorna su respuesta")
    void login_deberiaDelegarEnAuthClient() {
        // Given
        LoginRequest request = new LoginRequest("user1@mail.cl", "abcd.1234");
        when(authClient.login(request)).thenReturn(new AuthResponse("token-jwt"));

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertEquals("token-jwt", response.token());
        verify(authClient).login(request);
    }

    @Test
    @DisplayName("register: delega en AuthClient y retorna su respuesta")
    void register_deberiaDelegarEnAuthClient() {
        // Given
        RegisterRequest request = new RegisterRequest("nuevo@mail.cl", "abcd.1234");
        when(authClient.register(request)).thenReturn(new AuthResponse("token-nuevo"));

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertEquals("token-nuevo", response.token());
        verify(authClient).register(request);
    }
}
