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
    private String address;
    private String city;
    private String postalCode;
    private String country;

    private String residenceType; //house or flat
    private boolean isPrimary = false;
    private boolean active = true; //for residence deactivation

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    private LocalDate registrationDate = LocalDate.now();


}

