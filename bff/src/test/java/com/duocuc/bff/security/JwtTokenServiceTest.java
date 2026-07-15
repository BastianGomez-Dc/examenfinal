package com.duocuc.bff.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Verifica que JwtTokenService (usado por el filtro de seguridad del BFF) valida
 * correctamente tokens firmados con el mismo secret que usa ms-auth para emitirlos.
 */
class JwtTokenServiceTest {

    private static final String SECRET = "$&miSercreto.SuperSeguro!1988";

    @Test
    @DisplayName("extractSubject: retorna el email cuando el token fue firmado con el mismo secret")
    void extractSubject_deberiaRetornarEmail_cuandoTokenValido() throws Exception {
        // Given
        JwtTokenService jwtTokenService = new JwtTokenService(SECRET);
        String token = generarToken(SECRET, "user1@mail.cl");

        // When
        String subject = jwtTokenService.extractSubject(token);

        // Then
        assertEquals("user1@mail.cl", subject);
    }

    @Test
    @DisplayName("extractSubject: lanza JwtException cuando el token fue firmado con otro secret")
    void extractSubject_deberiaLanzarExcepcion_cuandoSecretDistinto() throws Exception {
        // Given
        JwtTokenService jwtTokenService = new JwtTokenService(SECRET);
        String token = generarToken("otro-secret-distinto", "user1@mail.cl");

        // When / Then
        assertThrows(JwtException.class, () -> jwtTokenService.extractSubject(token));
    }

    private String generarToken(String secret, String subject) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        SecretKey key = Keys.hmacShaKeyFor(digest.digest(secret.getBytes(StandardCharsets.UTF_8)));
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(Duration.ofHours(24))))
                .signWith(key)
                .compact();
    }
}
