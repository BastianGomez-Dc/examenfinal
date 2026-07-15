package com.duocuc.bff.services;

import com.duocuc.bff.client.AuthClient;
import com.duocuc.bff.dto.AuthResponse;
import com.duocuc.bff.dto.LoginRequest;
import com.duocuc.bff.dto.RegisterRequest;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthClient authClient;

    public AuthService(AuthClient authClient) {
        this.authClient = authClient;
    }

    public AuthResponse login(LoginRequest request) {
        return authClient.login(request);
    }

    public AuthResponse register(RegisterRequest request) {
        return authClient.register(request);
    }
}
