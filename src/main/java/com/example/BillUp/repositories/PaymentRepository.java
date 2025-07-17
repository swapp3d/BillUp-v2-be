package com.example.BillUp.repositories;

import com.example.BillUp.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByBillId(Long billId);
    List<Payment> findByUserIdAndBillId(Long userId, Long billId);
}