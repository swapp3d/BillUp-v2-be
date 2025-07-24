package com.example.BillUp.services;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import com.example.BillUp.repositories.TokenRepository;

import java.util.List;

@Service
public class LogoutService implements LogoutHandler {
    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public LogoutService(TokenRepository tokenRepository, JwtService jwtService, UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
       String header = request.getHeader("Authorization");
       if (header == null || !header.startsWith(BEARER_TOKEN_PREFIX)) {
           return;
       }
       String tokenFromHeader = header.substring(BEARER_TOKEN_PREFIX.length());
       Token storedToken = tokenRepository.findByToken(tokenFromHeader).orElse(null);

       if (storedToken == null || storedToken.isRevoked()) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           return;
       }

       String email = jwtService.extractEmail(tokenFromHeader);
       if (email == null) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           return;
       }

       User user = userRepository.findByEmail(email).orElse(null);
       if (user == null) {
           response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
           return;
       }

       List<Token> tokens = tokenRepository.findAllByUserAndRevokedFalse(user);
       for (Token token : tokens) {
           token.setRevoked(true);
           tokenRepository.save(token);
       }
    }
}
