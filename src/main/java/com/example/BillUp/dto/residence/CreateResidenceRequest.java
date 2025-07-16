package com.example.BillUp.dto.residence;

import lombok.Data;

@Data
public class CreateResidenceRequest {
    private String address;
    private String city;
    private String postalCode;
    private String country;
    private String residenceType;
    private boolean isPrimary;
}

