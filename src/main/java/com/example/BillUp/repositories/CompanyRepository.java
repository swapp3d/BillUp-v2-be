package com.example.BillUp.repositories;

import com.example.BillUp.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
    Optional<Company> findCompanyByCompanyEmail(String email);
}
