package com.example.BillUp.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {

    private Long id;

    private String name;

    private String surname;

    private String email;

    private String phoneNumber;
}
