package com.example.BillUp.repositories;

import com.example.BillUp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByBillId(Long billId);
    List<Payment> findByUserIdAndBillId(Long userId, Long billId);

    @Query(value = "SELECT * FROM payments", nativeQuery = true)
    List<Payment> findAllIncludingDeleted();

    @Query(value = "SELECT * FROM payments WHERE id = :id", nativeQuery = true)
    Optional<Payment> findByIdIncludingDeleted(@Param("id") Long id);
}