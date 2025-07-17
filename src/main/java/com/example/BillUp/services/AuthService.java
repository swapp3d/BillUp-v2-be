package com.example.BillUp.services;

import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.Role;
import com.example.BillUp.repositories.CompanyRepository;
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
    private final CompanyRepository companyRepository;

    public void register(RegisterRequestDTO request) {
        System.out.println("inside register service");
        if (request.getRole() == Role.CLIENT && request.getSurname().isBlank()) {
            System.out.println("checking the role CLIENT");
            throw new IllegalArgumentException("Surname is required for CLIENT role");
        }
        System.out.println("creating user");
        User user = User.builder()
                .role(request.getRole())
                .name(request.getName())
                .surname(request.getRole() == Role.COMPANY ? null : request.getSurname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(0.0)
                .build();

        System.out.println("saving user");
        userRepository.save(user);

        if (request.getRole() == Role.COMPANY) {
            System.out.println("creating company");
            Company company = Company.builder()
                    .name(request.getName())
                    .companyEmail(request.getEmail())
                    .companyNumber(request.getPhoneNumber())
                    .user(user)
                    .build();
            System.out.println("saving company");
            companyRepository.save(company);
        }
    }

    public User login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        return user;
    }
}
