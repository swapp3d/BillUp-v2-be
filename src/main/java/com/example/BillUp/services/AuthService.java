package com.example.BillUp.services;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.dto.authentication.LoginResponseDTO;
import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.dto.authentication.RegisterResponseDTO;
import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.entities.*;
import com.example.BillUp.enumerators.*;
import com.example.BillUp.exceptions.AlreadyLoggedInException;
import com.example.BillUp.exceptions.EmailAlreadyExistsException;
import com.example.BillUp.exceptions.PhoneNumberAlreadyExistsException;
import com.example.BillUp.repositories.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ResidenceRepository residenceRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    @Transactional
    public RegisterResponseDTO register(RegisterRequestDTO request) {
        validateRegisterRequest(request);
        isUserRegistered(request);

        User user = createUser(request);
        userRepository.save(user);

        Residence residence = maybeCreateResidence(request, user);
        maybeCreateCompany(request, user);

        return buildRegisterResponse(user, residence);
    }

    private void validateRegisterRequest(RegisterRequestDTO request) {
        if (request.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        if (request.getRole() == Role.CLIENT) {
            if (request.getSurname() == null || request.getSurname().isBlank()) {
                throw new IllegalArgumentException("Surname is required for CLIENT role");
            }
            if (request.getResidenceRequest() == null) {
                throw new IllegalArgumentException("Residence is required for CLIENT role");
            }
        }
    }

    private void isUserRegistered(RegisterRequestDTO request) {
        String email = request.getEmail();
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("User with email " + email + " already exists");
        }

        String phoneNumber = request.getPhoneNumber();
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new PhoneNumberAlreadyExistsException("User with phone number " + phoneNumber + " already exists");
        }
    }

    private User createUser(RegisterRequestDTO request) {
        return User.builder()
                .role(request.getRole())
                .name(request.getName())
                .surname(request.getRole() == Role.CLIENT ? request.getSurname() : null)
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(0.0)
                .build();
    }

    private Residence maybeCreateResidence(RegisterRequestDTO request, User user) {
        if (request.getRole() != Role.CLIENT) return null;

        CreateResidenceRequest r = request.getResidenceRequest();
        Residence residence = Residence.builder()
                .user(user)
                .streetAddress(r.getStreetAddress())
                .flatNumber(r.getFlatNumber())
                .city(r.getCity())
                .postalCode(r.getPostalCode())
                .country(r.getCountry())
                .residenceType(ResidenceType.valueOf(r.getResidenceType()))
                .isPrimary(true)
                .registrationDate(LocalDate.now())
                .build();

        return residenceRepository.save(residence);
    }

    private void maybeCreateCompany(RegisterRequestDTO request, User user) {
        if (request.getRole() != Role.COMPANY) return;

        Company company = Company.builder()
                .name(request.getName())
                .companyEmail(request.getEmail())
                .companyNumber(request.getPhoneNumber())
                .user(user)
                .build();

        companyRepository.save(company);
    }

    private RegisterResponseDTO buildRegisterResponse(User user, Residence residence) {
        RegisterResponseDTO.RegisterResponseDTOBuilder builder = RegisterResponseDTO.builder()
                .id(user.getId())
                .role(user.getRole())
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber());

        if (user.getRole() == Role.CLIENT && residence != null) {
            builder.residenceResponse(ResidenceResponse.builder()
                    .id(residence.getId())
                    .streetAddress(residence.getStreetAddress())
                    .flatNumber(residence.getFlatNumber())
                    .city(residence.getCity())
                    .postalCode(residence.getPostalCode())
                    .country(residence.getCountry())
                    .residenceType(residence.getResidenceType())
                    .isPrimary(residence.isPrimary())
                    .isActive(residence.isActive())
                    .fullAddress(residence.getFullAddress())
                    .build());
        }

        return builder.build();
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        List<Token> tokens = tokenRepository.findAllByUserAndRevokedFalse(user);
        if (!tokens.isEmpty()) {
            throw new AlreadyLoggedInException("User is already logged in");
        }

        return user;
    }

    public void refreshToken(User user, HttpServletResponse response) {
        try {
            revokeAccessToken(user);
            Optional<Token> oldRefreshTokenOpt = tokenRepository.findByUserAndRevokedFalse(user);

            if (oldRefreshTokenOpt.isEmpty()) {
                log.warn("Refresh token not found");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Token oldRefreshToken = oldRefreshTokenOpt.get();

            if (!jwtService.validateToken(oldRefreshToken.getToken(), user).isValid()) {
                log.warn("Refresh token is invalid");
                oldRefreshToken.setRevoked(true);
                tokenRepository.save(oldRefreshToken);
                respondWithJson(response, HttpServletResponse.SC_UNAUTHORIZED, Map.of(
                        "message", "You need to login",
                        "error", "Refresh token is invalid or expired"
                ));
                return;
            }

            oldRefreshToken.setRevoked(true);
            tokenRepository.save(oldRefreshToken);

            String newAccessToken = jwtService.generateToken(user);
            String newRefreshToken = jwtService.generateRefreshToken(user);

            tokenRepository.saveAll(List.of(
                    buildToken(newAccessToken, TokenType.ACCESS, user),
                    buildToken(newRefreshToken, TokenType.REFRESH, user)
            ));

            respondWithJson(response, HttpServletResponse.SC_OK,
                    new LoginResponseDTO(newAccessToken, newRefreshToken));

        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private void revokeAccessToken(User user) {
        List<Token> accessTokens = tokenRepository.findAllByUserAndRevokedFalseAndTokenType(user, TokenType.ACCESS);
        accessTokens.forEach(token -> {
            token.setRevoked(true);
            tokenRepository.save(token);
            log.info("Revoked access token: {}", token.getToken());
        });
    }

    private Token buildToken(String tokenStr, TokenType type, User user) {
        return Token.builder()
                .token(tokenStr)
                .tokenType(type)
                .expiryDate(jwtService.extractExpirationDate(tokenStr))
                .revoked(false)
                .user(user)
                .build();
    }

    private void respondWithJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setContentType("application/json");
        response.setStatus(status);
        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }

    private void handleError(HttpServletResponse response, Exception e) {
        log.error("Token refresh failed", e);
        try {
            respondWithJson(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Map.of(
                    "Token refresh failed :", e.getMessage()
            ));
        } catch (IOException ioException) {
            log.error("Failed to write error response", ioException);
        }
    }
}
