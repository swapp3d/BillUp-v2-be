package com.example.BillUp.services;

import com.example.BillUp.dto.authentication.CompanyRegisterRequestDTO;
import com.example.BillUp.dto.authentication.LoginRequestDTO;
import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.enums.Role;
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

    public void registerUser(RegisterRequestDTO request) {
        User user = User.builder()
                .role(Role.CLIENT)
                .name(request.getName())
                .surname(request.getSurname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .balance(0.0)
                .build();
        userRepository.save(user);
    }

    public User loginUser(String email, String rawPassword) {
        System.out.println("inside login service");
        User user = userRepository.findByEmail(email).orElse(null);
        System.out.println("user is: " + user);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            System.out.println("something wrong with user!!");
            throw new BadCredentialsException("Wrong email or password");
        }
        System.out.println("returning user");
        return user;
    }

    public void registerCompany(CompanyRegisterRequestDTO dto) {
        Company company = Company.builder()
                .name(dto.getName())
                .companyEmail(dto.getCompanyEmail())
                .passwordHash(passwordEncoder.encode(dto.getPassword()))
                .companyNumber(dto.getCompanyNumber())
                .build();

        companyRepository.save(company);
    }

    public Company loginCompany(LoginRequestDTO dto) {
        Company company = companyRepository.findCompanyByCompanyEmail(dto.getEmail()).orElse(null);
        if (company == null || !passwordEncoder.matches(dto.getPassword(), company.getPasswordHash())) {
            throw new BadCredentialsException("Wrong email or password");
        }
        return company;
    }
}
