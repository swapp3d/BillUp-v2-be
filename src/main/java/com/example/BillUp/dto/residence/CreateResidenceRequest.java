package com.example.BillUp.dto.residence;

import com.example.BillUp.enumerators.ResidenceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateResidenceRequest {

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String flatNumber;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "\\d{5}", message = "Postal code must be 5 digits")
    private String postalCode;

    @NotBlank(message = "Country is required")
    private String country;

    @NotNull(message = "Residence type is required")
    private ResidenceType residenceType;

    private boolean isPrimary;
}
