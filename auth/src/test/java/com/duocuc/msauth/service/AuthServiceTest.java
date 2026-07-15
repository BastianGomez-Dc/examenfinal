package com.duocuc.msauth.service;

import com.duocuc.msauth.domain.User;
import com.duocuc.msauth.dto.AuthResponse;
import com.duocuc.msauth.dto.LoginRequest;
import com.duocuc.msauth.dto.RegisterRequest;
import com.duocuc.msauth.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas unitarias del servicio de autenticación.
 * PasswordEncoder y JwtService se mockean para aislar la lógica de AuthService.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("user1@mail.cl", "hash-encriptado");
    }

    @Test
    @DisplayName("login: retorna un token cuando las credenciales son correctas")
    void login_deberiaRetornarToken_cuandoCredencialesCorrectas() {
        // Given
        LoginRequest request = new LoginRequest("user1@mail.cl", "abcd.1234");
        when(userRepository.findByEmailAndActiveTrue("user1@mail.cl")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("abcd.1234", "hash-encriptado")).thenReturn(true);
        when(jwtService.generateToken("user1@mail.cl")).thenReturn("token-jwt");

        // When
        AuthResponse response = authService.login(request);

        // Then
        assertEquals("token-jwt", response.token());
    }

    @Test
    @DisplayName("login: lanza 401 cuando el usuario no existe o está inactivo")
    void login_deberiaLanzar401_cuandoUsuarioNoExiste() {
        // Given
        LoginRequest request = new LoginRequest("no-existe@mail.cl", "abcd.1234");
        when(userRepository.findByEmailAndActiveTrue("no-existe@mail.cl")).thenReturn(Optional.empty());

        // When / Then
        assertThrows(ResponseStatusException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("login: lanza 401 cuando la contraseña es incorrecta")
    void login_deberiaLanzar401_cuandoPasswordIncorrecta() {
        // Given
        LoginRequest request = new LoginRequest("user1@mail.cl", "password-mala");
        when(userRepository.findByEmailAndActiveTrue("user1@mail.cl")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password-mala", "hash-encriptado")).thenReturn(false);

        // When / Then
        assertThrows(ResponseStatusException.class, () -> authService.login(request));
        verify(jwtService, never()).generateToken(anyString());
    }

    @Test
    @DisplayName("register: crea el usuario y retorna un token cuando el email no existe")
    void register_deberiaCrearUsuario_cuandoEmailNoExiste() {
        // Given
        RegisterRequest request = new RegisterRequest("nuevo@mail.cl", "abcd.1234");
        when(userRepository.existsById("nuevo@mail.cl")).thenReturn(false);
        when(passwordEncoder.encode("abcd.1234")).thenReturn("hash-nuevo");
        when(jwtService.generateToken("nuevo@mail.cl")).thenReturn("token-nuevo");

        // When
        AuthResponse response = authService.register(request);

        // Then
        assertEquals("token-nuevo", response.token());
        verify(userRepository).save(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    @DisplayName("register: lanza 409 cuando el email ya está registrado")
    void register_deberiaLanzar409_cuandoEmailYaRegistrado() {
        // Given
        RegisterRequest request = new RegisterRequest("user1@mail.cl", "abcd.1234");
        when(userRepository.existsById("user1@mail.cl")).thenReturn(true);

        // When / Then
        assertThrows(ResponseStatusException.class, () -> authService.register(request));
        verify(userRepository, never()).save(org.mockito.ArgumentMatchers.any(User.class));
    }
}
