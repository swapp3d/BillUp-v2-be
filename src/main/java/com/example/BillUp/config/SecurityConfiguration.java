package com.example.BillUp.config;

import com.example.BillUp.config.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtAuthFilter jwtAuthFilter;

//    private final FilterChainExceptionHandler filterChainExceptionHandler;
//
//    private final AuthenticationProvider authenticationProvider;
//
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

//    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(permittedEndpoints).permitAll()
                        .anyRequest().authenticated()
                )
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
//                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
//                .addFilterBefore(filterChainExceptionHandler, LogoutFilter.class)
//                .logout(logout ->
//                        logout.logoutUrl("/api/v1/auth/logout").
//                                addLogoutHandler(logoutHandler).
//                                logoutSuccessHandler((request, response, authentication) ->
//                                        SecurityContextHolder.clearContext()
//                                ));

        return http.build();
    }

    private final String[] permittedEndpoints = {
            "/api/v1/auth/**",
    };
}