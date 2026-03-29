package com.example.BillUp.controllers;

import com.example.BillUp.dto.payment.AdminPaymentResponseDTO;
import com.example.BillUp.dto.payment.AdminPaymentUpdateDTO;
import com.example.BillUp.dto.payment.PaymentRequest;
import com.example.BillUp.dto.payment.PaymentResponse;
import com.example.BillUp.entities.Payment;
import com.example.BillUp.services.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/v1/payment")

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    //Creation of Payment (CLIENT)
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(
            @Valid
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        System.out.println("inside payment controller");
        PaymentResponse response = paymentService.processPayment(request, authHeader);
        return ResponseEntity.ok(response);
    }

    //All Payments Tableview (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminPaymentResponseDTO>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    //Editing Payments (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<AdminPaymentResponseDTO> updatePayment(
            @PathVariable Long id,
            @RequestBody AdminPaymentUpdateDTO dto) {

        return ResponseEntity.ok(
                paymentService.adminUpdatePayment(id, dto)
        );
    }

    //Deleting Payments (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    //Restoring Payments (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<Payment> restorePayment(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.restorePayment(id));
    }
}
