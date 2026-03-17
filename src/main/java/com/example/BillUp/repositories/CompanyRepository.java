package com.example.BillUp.repositories;

import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUser(User user);

}