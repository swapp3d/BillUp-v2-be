package com.example.BillUp.dto.residence;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminCreateResidenceDTO {

    @NotNull(message = "User ID is required")
    private Long userId;

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