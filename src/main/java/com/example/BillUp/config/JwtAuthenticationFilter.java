package com.example.BillUp.config;

import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.services.SecurityJwtService;
import io.micrometer.common.lang.NonNull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_TOKEN_PREFIX = "Bearer ";

    private final SecurityJwtService securityJwtService;

    private final UserDetailsService userDetailsService;

    private final TokenRepository tokenRepository;


    public JwtAuthenticationFilter(SecurityJwtService securityJwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
        this.securityJwtService = securityJwtService;
        this.userDetailsService = userDetailsService;
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

    }
}
