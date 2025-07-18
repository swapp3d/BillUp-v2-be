package com.example.BillUp.services;

import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.ResidenceType;
import com.example.BillUp.enumerators.Role;
import com.example.BillUp.exceptions.EmailAlreadyExistsException;
import com.example.BillUp.repositories.CompanyRepository;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.UserRepository;
import com.fasterxml.jackson.databind.DatabindException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ResidenceRepository residenceRepository;

    public User register(RegisterRequestDTO request) {
        System.out.println("inside the registration service");
        isUserRegistered(request.getEmail());

        if (request.getRole() == Role.CLIENT && request.getSurname().isBlank()) {
            throw new IllegalArgumentException("Surname is required for CLIENT role");
        }
        System.out.println("creating user");
        User user = createUser(request);
        System.out.println("saving user");
        userRepository.save(user);

        if (request.getRole() == Role.CLIENT) {
            System.out.println("creating residence");
            CreateResidenceRequest req = request.getResidenceRequest();
            System.out.println("is primary: " + req.isPrimary());
            Residence residence = Residence.builder()
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

        return user;
    }

    private void isUserRegistered(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException("User with email " + email + " already exists");
        }
    }

    private User createUser(RegisterRequestDTO request) {
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
        return user;
    }
}
