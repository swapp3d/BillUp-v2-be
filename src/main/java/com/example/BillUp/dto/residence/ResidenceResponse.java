package com.example.BillUp.dto.residence;

import com.example.BillUp.enumerators.ResidenceType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResidenceResponse {
    private Long id;
    private Long userId;
    private String streetAddress;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String country;
    private ResidenceType residenceType;
    private boolean isPrimary;
    private boolean isActive;
    private boolean deleted;
    private String fullAddress;
}

