package com.example.BillUp.dto.authentication;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRegisterRequestDTO {
    @NotEmpty(message = "name is required!")
    private String name;

    @NotEmpty(message = "email is required!")
    private String companyEmail;

    @NotEmpty(message = "number is required!")
    private String companyNumber;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit!")
    private String password;
}
