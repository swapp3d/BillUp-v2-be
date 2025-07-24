package com.example.BillUp.controllers;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.dto.authentication.*;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.TokenType;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        Token accessTokenEntity = Token.builder()
                .token(accessToken)
                .tokenType(TokenType.ACCESS)
                .expiryDate(jwtService.extractExpirationDate(accessToken))
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(accessTokenEntity);

        Token refreshTokenEntity = Token.builder()
                .token(refreshToken)
                .tokenType(TokenType.REFRESH)
                .expiryDate(jwtService.extractExpirationDate(refreshToken))
                .revoked(false)
                .user(user)
                .build();

        tokenRepository.save(refreshTokenEntity);
        return ResponseEntity.ok(new LoginResponseDTO(accessToken, refreshToken));
    }
}
