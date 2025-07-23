package com.example.BillUp.dto.residence;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class CreateResidenceRequest {
    @NotEmpty(message = "street address is required")
    private String streetAddress;

    private String flatNumber;

    @NotEmpty(message = "city is required")
    private String city;

    @NotEmpty(message = "postal code is required")
    private String postalCode;

    @NotEmpty(message = "country is required")
    private String country;

    @NotEmpty(message = "residence type is required")
    private String residenceType;

    private boolean isPrimary;
}


