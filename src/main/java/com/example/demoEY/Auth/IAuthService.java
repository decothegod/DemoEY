package com.example.demoEY.Auth;

import org.springframework.http.ResponseEntity;

public interface IAuthService {
    ResponseEntity<AuthResponse> login(LoginRequest request);
    ResponseEntity<AuthResponse> register(RegisterRequest request);



}
