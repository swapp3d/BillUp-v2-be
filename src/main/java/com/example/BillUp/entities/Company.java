package com.example.BillUp.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "companies")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE companies SET deleted = true WHERE id = ?")
public class Company {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Email
    private String companyEmail;

    @Column(nullable = false)
    private String companyNumber;

    @Column(nullable = false)
    private boolean deleted = false;

    @OneToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = true, unique = true)
    private User user;

    @OneToMany(mappedBy = "company")
    private List<Bill> bills;
}