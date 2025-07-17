package com.example.BillUp.config;

//import com.example.BillUp.repositories.TokenRepository;
//import com.example.BillUp.services.JwtService;
//import io.micrometer.common.lang.NonNull;
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//import com.example.BillUp.entities.Token;
//
//import java.io.IOException;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//    private static final String BEARER_TOKEN_PREFIX = "Bearer ";
//
//    private final JwtService jwtService;
//
//    private final UserDetailsService userDetailsService;
//
//    private final TokenRepository tokenRepository;
//
//
//    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService, TokenRepository tokenRepository) {
//        this.jwtService = jwtService;
//        this.userDetailsService = userDetailsService;
//        this.tokenRepository = tokenRepository;
//    }
//
//    @Override
//    protected void doFilterInternal(@NonNull HttpServletRequest request,
//                                    @NonNull HttpServletResponse response,
//                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
//
//    }
//}
