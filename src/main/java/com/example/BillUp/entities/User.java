package com.example.BillUp.entities;

import com.example.BillUp.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Enumerated(EnumType.STRING)
    private Role role;
    private String name;
    private String surname;
    //address attribute will be replaced by list of residences (new entity)
    private String email;
    private String phoneNumber;
    private String password;
}
