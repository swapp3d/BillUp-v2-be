package com.example.BillUp.services;

import com.example.BillUp.dto.payment.PaymentRequest;
import com.example.BillUp.dto.payment.PaymentResponse;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.Payment;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.repositories.BillRepository;
import com.example.BillUp.repositories.PaymentRepository;
import com.example.BillUp.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BillRepository billRepository;
    private final UserRepository userRepository;

    @Transactional
    public Payment processBillPayment(Long billId, Long userId, Double amount, String provider, String methodToken) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!bill.getUser().getId().equals(userId)) {
            throw new RuntimeException("User is not authorized to pay this bill");
        }

        if (bill.getStatus() == BillStatus.PAID) {
            throw new RuntimeException("Bill is already paid");
        }

        Double remainingAmount = bill.getRemainingAmount();
        if (amount > remainingAmount) {
            throw new RuntimeException("Payment amount exceeds remaining bill amount");
        }

        String transactionId = "sandbox_" + UUID.randomUUID();

        Payment payment = Payment.builder()
                .amount(amount)
                .timestamp(LocalDateTime.now())
                .provider(provider)
                .transactionId(transactionId)
                .success(true)
                .methodToken(methodToken)
                .user(user)
                .bill(bill)
                .build();

        paymentRepository.save(payment);
        System.out.println("the payment: " + payment.getAmount());
        paymentRepository.flush();

        Bill updatedBill = billRepository.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));
        System.out.println("bill amount: " + updatedBill.getAmount());
        remainingAmount -= amount;
        updatedBill.setAmount(remainingAmount);
        billRepository.save(updatedBill);
        System.out.println("bill amount after pay: " + updatedBill.getAmount());

        if (updatedBill.isFullyPaid()) {
            updatedBill.setStatus(BillStatus.PAID);
            log.info("Before save: bill status = {}", updatedBill.getStatus());
            billRepository.save(updatedBill);
            log.info("After save: bill status = {}", updatedBill.getStatus());
        }

        return payment;
    }

    public PaymentResponse processPayment(PaymentRequest request) {
        Payment payment = processBillPayment(
                request.getBillId(),
                request.getUserId(),
                request.getAmount(),
                request.getProvider(),
                request.getMethodToken()
        );

        return new PaymentResponse(
                true,
                "Transaction successful",
                payment.getTransactionId(),
                payment.getAmount(),
                payment.getBill().getName(),
                payment.getBill().getId()
        );
    }
}
