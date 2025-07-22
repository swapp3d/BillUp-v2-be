package com.example.BillUp.dto.residence;

import com.example.BillUp.enumerators.ResidenceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResidenceResponse {
    private Long id;
    private String streetAddress;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String country;
    private ResidenceType residenceType;
    private boolean isPrimary;
    private boolean isActive;
    private String fullAddress;
}

