package com.example.BillUp.config.jwt;

import com.example.BillUp.dto.authentication.TokenValidationResult;
import com.example.BillUp.entities.User;
import com.example.BillUp.exceptions.InvalidJwtException;
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
import java.util.Map;

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
        try {
            String servletPath = request.getServletPath();
            System.out.println(servletPath);

            System.out.println("inside the jwt filter");
            String authHeader = request.getHeader("Authorization");
            String jwt = extractJwtToken(authHeader);
            if (jwt == null) {
                System.out.println("your jwt is null");
                filterChain.doFilter(request, response);
                return;
            }
            String email = jwtService.extractEmail(jwt);

            if (email == null) {
                filterChain.doFilter(request, response);
                return;
            }
            System.out.println("email found");

            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }
            System.out.println("user found");

            TokenValidationResult result = jwtService.validateToken(jwt, user);
            boolean isAccessValid = result.isValid();
            boolean isAccessRevoked = result.isRevoked();
            if (isAccessValid && SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.emptyList()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            } else if (!isAccessValid && !isAccessRevoked) {
                System.out.println("you need to refresh your access token");
                authService.refreshToken(user, response);
                return;
            }
            filterChain.doFilter(request, response);
        } catch (InvalidJwtException ex) {
            System.out.println("inside an exception");
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(Map.of("error", ex.getMessage()));

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(json);
        }
    }

    private String extractJwtToken (String authHeader) {
        if (authHeader == null) return null;

        authHeader = authHeader.trim();
        if (authHeader.toLowerCase().startsWith("bearer ")) {
            return authHeader.substring(7).trim();
        }
        return null;
    }
}
