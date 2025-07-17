package com.example.BillUp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "residences")
@Getter
@Setter
public class Residence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street_address", nullable = false, length = 255)
    private String streetAddress;

    @Column(name = "flat_number", length = 50)
    private String flatNumber;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "residence_type", nullable = false, length = 50)
    private String residenceType; // "house" or "flat"

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate = LocalDate.now();

    @Transient
    public String getFullAddress() {
        if (flatNumber != null && !flatNumber.isBlank()) {
            return "Flat " + flatNumber + ", " + streetAddress + ", " + city + ", " + postalCode + ", " + country;
        }
        return streetAddress + ", " + city + ", " + postalCode + ", " + country;
    }
}
