package com.example.BillUp.dto.residence;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateResidenceDTO {

    private String streetAddress;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String country;
    private String residenceType;

    private Boolean isPrimary;
    private Boolean isActive;
}