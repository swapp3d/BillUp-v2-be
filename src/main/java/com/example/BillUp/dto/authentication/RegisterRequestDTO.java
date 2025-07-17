package com.example.BillUp.dto.authentication;

import com.example.BillUp.enumerators.Role;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDTO {

    @NotEmpty(message = "role is required")
    private Role role;

    @NotEmpty(message = "name is required!")
    private String name;

    //in serviceAuth
    private String surname;

    @NotEmpty(message = "email is required!")
    private String email;

    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit!")
    private String password;

    @NotEmpty(message = "phone number is required!")
    private String phoneNumber;
}
