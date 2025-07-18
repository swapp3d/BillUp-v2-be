package com.example.BillUp.dto.residence;

import lombok.Data;

@Data
public class ResidenceResponse {
    private Long id;
    private String streetAddress;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String country;
    private String residenceType;
    private boolean isPrimary;
    private boolean isActive;
    private String fullAddress;
}

