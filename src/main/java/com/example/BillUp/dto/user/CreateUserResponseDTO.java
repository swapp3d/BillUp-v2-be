package com.example.BillUp.dto.user;

import com.example.BillUp.enumerators.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateUserResponseDTO {

    private Long id;
    private Role role;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
}