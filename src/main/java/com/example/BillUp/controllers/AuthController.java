package com.example.BillUp.controllers;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.dto.authentication.*;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.TokenType;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.View;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final View error;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO request) {
        try {
            User user = authService.register(request);

            RegisterResponseDTO response = new RegisterResponseDTO(
                    user.getId(),
                    user.getName(),
                    user.getSurname(),
                    user.getEmail(),
                    user.getPhoneNumber()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body( Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
//        if (SecurityContextHolder.getContext().getAuthentication() != null) {
//            //TODO
//        }

        User user = authService.login(loginRequest.getEmail(), loginRequest.getPassword());
        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

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
