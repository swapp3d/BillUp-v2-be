package com.example.BillUp.services;

import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.Role;
import com.example.BillUp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void register(RegisterRequestDTO request) {
        if (request.getRole() == Role.CLIENT && request.getSurname().isBlank()) {
            throw new IllegalArgumentException("Surname is required for CLIENT role");
        }
        User user = User.builder()
                .role(request.getRole())
                .name(request.getName())
                .surname(request.getRole() == Role.COMPANY ? null : request.getSurname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(0.0)
                .build();

        //creating company entity

        userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        return user;
    }
}
