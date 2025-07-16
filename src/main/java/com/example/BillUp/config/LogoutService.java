package com.example.BillUp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.services.*;

@Service
public class LogoutService implements LogoutHandler {
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final TokenRepository tokenRepository;

    public LogoutService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String jwt;

        if (authHeader == null || !authHeader.startsWith(BEARER_TOKEN_PREFIX)) {
            return;
        }
        jwt = authHeader.substring(BEARER_TOKEN_PREFIX.length());
        var storedToken = tokenRepository.findByToken(jwt).orElse(null);

        if (storedToken != null) {
            storedToken.setRevoked(true);
            tokenRepository.save(storedToken);
        }
    }
}
