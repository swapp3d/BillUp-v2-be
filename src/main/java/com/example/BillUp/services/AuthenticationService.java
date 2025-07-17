package com.example.BillUp.services;

import com.example.BillUp.dto.authentication.LoginRequestDTO;
import com.example.BillUp.dto.authentication.LoginResponseDTO;
import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.dto.authentication.RegisterResponseDTO;
import com.example.BillUp.entities.Token;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.TokenType;
import com.example.BillUp.exceptions.EmailAlreadyExistsException;
import com.example.BillUp.repositories.TokenRepository;
import com.example.BillUp.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecurityJwtService securityJwtService;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder, SecurityJwtService securityJwtService, AuthenticationManager authenticationManager){
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityJwtService = securityJwtService;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponseDTO register(RegisterRequestDTO registerRequest) {

        if (isUniqueUser(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email " + registerRequest.getEmail() + " already exists");
        }

        var user = User.builder()
                .name(registerRequest.getName())
                .surname(registerRequest.getSurname())
                .passwordHash(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .phoneNumber(registerRequest.getPhoneNumber())
                .build();
        User savedUser = userRepository.save(user);

        return new RegisterResponseDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getSurname(),
                savedUser.getEmail(),
                savedUser.getPhoneNumber()
        );
    }
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {

        User user = selectUser(loginRequest);

        userCredentialsMatch(loginRequest, user);

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        var jwtToken = securityJwtService.generateToken(user);

        revokeAllUserTokens(user);
        saveToken(user, jwtToken, TokenType.BEARER);

        return new LoginResponseDTO(jwtToken);
    }
    public Boolean isUserUnique(String email){
        return userRepository.existsByEmail(email);
    }

    public void saveToken(User user, String jwtToken, TokenType tokenType){
        Token token = Token.builder()
                .token(jwtToken)
                .tokenType(tokenType)
                .expiryDate(securityJwtService.extractClaim(jwtToken, Claims::getExpiration))
                .revoked(false)
                .user(user)
                .build();
        tokenRepository.save(token);
    }
    private User selectUser(LoginRequestDTO loginRequest) {
        return userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email not found"));
    }

    private Boolean isUniqueUser(String email) {
        return userRepository.existsByEmail(email);
    }

    private void revokeAllUserTokens(User user) {
        var validTokens = tokenRepository.findAllValidTokensOfUser(user.getId());

        if (validTokens.isEmpty()) {
            return;
        }
        validTokens.forEach(token -> {
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validTokens);
    }

    private void userCredentialsMatch(LoginRequestDTO loginRequestDTO, User user){
        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())){
            throw new BadCredentialsException("Wrong password");
        }
    }
}
