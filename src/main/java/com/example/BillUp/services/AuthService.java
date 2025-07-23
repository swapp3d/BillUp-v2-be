package com.example.BillUp.services;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.dto.authentication.LoginResponseDTO;
import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.dto.authentication.RegisterResponseDTO;
import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.ResidenceType;
import com.example.BillUp.enumerators.Role;
import com.example.BillUp.enumerators.TokenType;
import com.example.BillUp.exceptions.EmailAlreadyExistsException;
import com.example.BillUp.repositories.CompanyRepository;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.repositories.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ResidenceRepository residenceRepository;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;

    public RegisterResponseDTO register(RegisterRequestDTO request) {
        System.out.println("inside the registration service");
        isUserRegistered(request.getEmail());

        if (request.getRole() == Role.CLIENT && request.getSurname().isBlank()) {
            throw new IllegalArgumentException("Surname is required for CLIENT role");
        }
        System.out.println("creating user");
        User user = createClient(request);
        System.out.println("saving user");
        userRepository.save(user);

        Residence residence = null;
        if (request.getRole() == Role.CLIENT) {
            System.out.println("creating residence");
            CreateResidenceRequest req = request.getResidenceRequest();
            System.out.println("is primary: " + req.isPrimary());
            residence = Residence.builder()
                    .user(user)
                    .streetAddress(req.getStreetAddress())
                    .flatNumber(req.getFlatNumber())
                    .city(req.getCity())
                    .postalCode(req.getPostalCode())
                    .country(req.getCountry())
                    .residenceType(ResidenceType.valueOf(req.getResidenceType()))
                    .isPrimary(true)
                    .registrationDate(LocalDate.now())
                    .build();

            System.out.println("saving residence " + residence);
            residenceRepository.save(residence);
        }
        if (request.getRole() == Role.COMPANY) {
            Company company = createCompany(request, user);
            companyRepository.save(company);
        }

        return createResponse(user, residence);
    }

    private RegisterResponseDTO createResponse(User user, Residence residence) {
        if (user.getRole() == Role.CLIENT) {
            ResidenceResponse residenceResponse = ResidenceResponse.builder()
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
                    .build();

            return RegisterResponseDTO.builder()
                    .id(user.getId())
                    .role(user.getRole())
                    .name(user.getName())
                    .surname(user.getSurname())
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .residenceResponse(residenceResponse)
                    .build();
        } else if (user.getRole() == Role.COMPANY) {
            return RegisterResponseDTO.builder()
                    .id(user.getId())
                    .role(user.getRole())
                    .name(user.getName())
                    .surname(null)
                    .email(user.getEmail())
                    .phoneNumber(user.getPhoneNumber())
                    .residenceResponse(null)
                    .build();
        }
        return null;
    }

    private void isUserRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }

    private User createClient(RegisterRequestDTO request) {
        return User.builder()
                .role(request.getRole())
                .name(request.getName())
                .surname(request.getRole() == Role.COMPANY ? null : request.getSurname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(0.0)
                .build();
    }

    private Company createCompany(RegisterRequestDTO request, User user) {
        return Company.builder()
                .name(request.getName())
                .companyEmail(request.getEmail())
                .companyNumber(request.getPhoneNumber())
                .user(user)
                .build();
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        List<Token> tokens = tokenRepository.findAllByUserAndRevokedFalse(user);
        tokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(tokens);
        return user;
    }

    public void refreshToken(User user, HttpServletResponse response) {
        try {
            revokeAccessToken(user);
            Optional<Token> oldRefreshTokenOpt = tokenRepository.findByUserAndRevokedFalse(user);

            if (oldRefreshTokenOpt.isEmpty()) {
                System.out.println("refresh token not found");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            Token oldRefreshToken = oldRefreshTokenOpt.get();

            if (!jwtService.isTokenValid(oldRefreshToken.getToken(), user)) {
                System.out.println("refresh token is not valid");
                oldRefreshToken.setRevoked(true);
                tokenRepository.save(oldRefreshToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                Map<String, String> errorResponse = Map.of(
                        "message", "You need to login",
                        "error", "Refresh token is invalid or expired"
                );
                new ObjectMapper().writeValue(response.getOutputStream(), errorResponse);
                return;
            }

            System.out.println("refresh token is valid");
            oldRefreshToken.setRevoked(true);
            tokenRepository.save(oldRefreshToken);

            String newAccessToken = jwtService.generateToken(user.getEmail());
            String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

            Token newAccessTokenEntity = Token.builder()
                    .token(newAccessToken)
                    .tokenType(TokenType.ACCESS)
                    .expiryDate(jwtService.extractExpirationDate(newAccessToken))
                    .revoked(false)
                    .user(user)
                    .build();
            tokenRepository.save(newAccessTokenEntity);

            Token newRefreshTokenEntity = Token.builder()
                    .token(newRefreshToken)
                    .tokenType(TokenType.REFRESH)
                    .expiryDate(jwtService.extractExpirationDate(newRefreshToken))
                    .revoked(false)
                    .user(user)
                    .build();

            tokenRepository.save(newRefreshTokenEntity);

            LoginResponseDTO tokens = new LoginResponseDTO(newAccessToken, newRefreshToken);

            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_OK);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception e) {
            try {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write("Token refresh failed " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void revokeAccessToken(User user) {
        System.out.println("inside accessToken revoke");
        List<Token> accessTokens = tokenRepository.findAllByUserAndRevokedFalseAndTokenType(user, TokenType.ACCESS);

        for (Token token : accessTokens) {
            token.setRevoked(true);
            tokenRepository.save(token);
            System.out.println("access token revoked: " + token.getToken());
        }

        if (accessTokens.isEmpty()) {
            System.out.println("No active access tokens found");
        }
    }

}
