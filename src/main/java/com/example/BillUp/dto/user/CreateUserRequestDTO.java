package com.example.BillUp.dto.user;

import com.example.BillUp.enumerators.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateUserRequestDTO {

    @NotNull(message = "Role is required")
    private Role role;

    @NotBlank(message = "Name is required")
    @Size(max = 50)
    private String name;

    private String surname;

    @NotBlank(message = "Email is required")
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8)
    private String password;

    @NotBlank(message = "Phone number is required")
    @Pattern(
            regexp = "^\\+[1-9]\\d{7,14}$",
            message = "Phone number must be in E.164 format"
    )
    private String phoneNumber;
}