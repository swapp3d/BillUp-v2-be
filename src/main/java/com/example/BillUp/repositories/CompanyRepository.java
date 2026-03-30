package com.example.BillUp.repositories;

import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByUser(User user);

    @Query(value = "SELECT * FROM companies", nativeQuery = true)
    List<Company> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM companies WHERE id = :id", nativeQuery = true)
    Optional<Company> findByIdIncludingDeleted(@Param("id") Long id);

    @Modifying
    @Query(value = "UPDATE companies SET deleted = true WHERE id = :id", nativeQuery = true)
    void softDeleteById(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) > 0 FROM companies WHERE id = :id", nativeQuery = true)
    boolean existsByIdNative(@Param("id") Long id);
}