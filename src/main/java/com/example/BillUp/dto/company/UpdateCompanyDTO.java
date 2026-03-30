package com.example.BillUp.dto.company;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCompanyDTO {
    private String name;
    private String companyEmail;
    private String companyNumber;
}