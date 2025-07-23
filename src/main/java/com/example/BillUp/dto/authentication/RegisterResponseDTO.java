package com.example.BillUp.dto.authentication;


import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.enumerators.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponseDTO {
    private Long id;
    private Role role;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private ResidenceResponse residenceResponse;
}
