package com.example.BillUp.dto.user;

import com.example.BillUp.enumerators.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserResponseDTO {

    private Long id;
    private Role role;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
}