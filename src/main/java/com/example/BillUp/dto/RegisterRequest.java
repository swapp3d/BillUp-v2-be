package com.example.BillUp.dto;

import com.example.BillUp.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private Role role;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private String password;
}
