package com.example.BillUp.dto.residence;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateResidenceRequest {
    @NotBlank(message = "street address is required")
    private String streetAddress;

    private String flatNumber;

    @NotBlank(message = "city is required")
    private String city;

    @NotBlank(message = "postal code is required")
    @Pattern(
            regexp = "^[A-Za-z0-9\\s\\-]{3,10}$",
            message = "Invalid postal code format"
    )
    private String postalCode;

    @NotBlank(message = "country is required")
    private String country;

    @NotBlank(message = "residence type is required")
    private String residenceType;

    private boolean isPrimary;
}


