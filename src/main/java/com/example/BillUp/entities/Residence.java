package com.example.BillUp.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Residence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String streetAddress;
    private String flatNumber;
    private String city;
    private String postalCode;
    private String country;

    public String getFullAddress() {
        if (flatNumber != null && !flatNumber.isBlank()) {
            return "Flat " + flatNumber + ", " + streetAddress + ", " + city + ", " + postalCode + ", " + country;
        }
        return streetAddress + ", " + city + ", " + postalCode + ", " + country;
    }


    private String residenceType; //house or flat
    private boolean isPrimary = false; //for prioritization of multiple residences
    private boolean active = true; //for residence deactivation

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDate registrationDate = LocalDate.now();


}

