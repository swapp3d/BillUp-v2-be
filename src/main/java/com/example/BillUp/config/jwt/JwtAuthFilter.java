package com.example.BillUp.config.jwt;

import com.example.BillUp.dto.authentication.LoginResponseDTO;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.TokenType;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.repositories.UserRepository;
import com.example.BillUp.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        System.out.println("inside the jwt filter");
        String authHeader = request.getHeader("Authorization");
        System.out.println("extracted auth header");
        String jwt = extractJwtToken(authHeader);
        System.out.println("extracted jwt token " + jwt);
        if (jwt == null) {
            System.out.println("bruh, your jwt is null");
            filterChain.doFilter(request, response);
            System.out.println("out of some filter ;/");
            return;
        }
        System.out.println("maybe we have problem with jwt service due updates");
        String email = jwtService.extractEmailExpired(jwt);
        System.out.println("extracted an email: " + email);

        if (email != null) {
            System.out.println("email found");
            User user = userRepository.findByEmail(email).orElse(null);
            if (user != null) {
                System.out.println("user found");
                boolean isAccessValid = jwtService.isAccessTokenValid(jwt, user);
                if (isAccessValid && SecurityContextHolder.getContext().getAuthentication() == null) {
                    System.out.println("logging in");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else if (!isAccessValid) {
                    System.out.println("you need to refresh your access token");
                    authService.refreshToken(user, response);
                    return;
                }
            }
            filterChain.doFilter(request, response);
        }
    }

    private String extractJwtToken (String authHeader){
        if (authHeader == null) return null;

        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
