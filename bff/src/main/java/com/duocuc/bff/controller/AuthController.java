package com.duocuc.bff.controller;

import com.duocuc.bff.dto.AuthResponse;
import com.duocuc.bff.dto.LoginRequest;
import com.duocuc.bff.dto.RegisterRequest;
import com.duocuc.bff.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Tag(name = "Auth", description = "Login y registro, delegados a ms-auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesión (proxy hacia ms-auth)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login exitoso, retorna el JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Registrar un nuevo usuario (proxy hacia ms-auth)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registro exitoso, retorna el JWT"),
            @ApiResponse(responseCode = "409", description = "El email ya está registrado")
    })
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }
}
