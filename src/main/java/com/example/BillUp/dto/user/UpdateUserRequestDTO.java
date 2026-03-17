package com.example.BillUp.dto.user;

import com.example.BillUp.enumerators.Role;
import lombok.Data;

@Data
public class UpdateUserRequestDTO {

    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private Role role;
    // change primary residence
    private Long primaryResidenceId;

}