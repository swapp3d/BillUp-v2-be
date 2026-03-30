package com.example.BillUp.dto.company;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyResponseDTO {
    private Long id;
    private Long userId;
    private String name;
    private String companyEmail;
    private String companyNumber;
    private boolean deleted;
}