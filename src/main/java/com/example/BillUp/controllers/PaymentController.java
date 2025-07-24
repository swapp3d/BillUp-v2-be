package com.example.BillUp.controllers;

import com.example.BillUp.dto.payment.PaymentRequest;
import com.example.BillUp.dto.payment.PaymentResponse;
import com.example.BillUp.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController

@RequestMapping("/api/v1/payments")

public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResponse> pay(
            @RequestBody PaymentRequest request,
            @RequestHeader("Authorization") String authHeader
    ) {
        PaymentResponse response = paymentService.processPayment(request, authHeader);
        return ResponseEntity.ok(response);
    }
}
