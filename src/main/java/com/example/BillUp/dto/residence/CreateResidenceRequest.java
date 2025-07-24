package com.example.BillUp.dto.residence;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResidenceRequest {
    @NotBlank(message = "street address is required")
    private String streetAddress;

    private String flatNumber;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "postal code is required")
    private String postalCode;

    @NotBlank(message = "country is required")
    private String country;

    @NotBlank(message = "residence type is required")
    private String residenceType;

    private boolean isPrimary;
}


