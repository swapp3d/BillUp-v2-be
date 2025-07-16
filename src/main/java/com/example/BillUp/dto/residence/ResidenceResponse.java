package com.example.BillUp.dto.residence;

import lombok.Data;

@Data
public class ResidenceResponse {
    private Long id;
    private String address;
    private String city;
    private String country;
    private boolean isPrimary;
    private boolean active;
}
