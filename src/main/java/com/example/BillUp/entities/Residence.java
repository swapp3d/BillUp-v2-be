package com.example.BillUp.entities;

import com.example.BillUp.enumerators.ResidenceType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "residences")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE residences SET deleted = true WHERE id = ?")
public class Residence {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false)
    private boolean deleted = false;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "residence_type", nullable = false, length = 50)
    private ResidenceType residenceType;

    @Column(name = "is_primary", nullable = false)
    private boolean isPrimary;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

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