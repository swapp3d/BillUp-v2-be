package com.example.BillUp.controllers;

import com.example.BillUp.config.jwt.JwtService;
import com.example.BillUp.dto.authentication.CompanyRegisterRequestDTO;
import com.example.BillUp.dto.authentication.LoginRequestDTO;
import com.example.BillUp.dto.authentication.LoginResponseDTO;
import com.example.BillUp.dto.authentication.RegisterRequestDTO;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class authController {
    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        authService.registerUser(registerRequest);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/login/user")
    public ResponseEntity<LoginResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        User user = authService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register/company")
    public ResponseEntity<String> registerCompany(@RequestBody CompanyRegisterRequestDTO registerRequest) {
        authService.registerCompany(registerRequest);
        return ResponseEntity.ok("success");
    }

    @PostMapping("/login/company")
    public ResponseEntity<LoginResponseDTO> loginCompany(LoginRequestDTO loginRequest) {
        Company company = authService.loginCompany(loginRequest);
        String token = jwtService.generateToken(company.getCompanyEmail());
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
