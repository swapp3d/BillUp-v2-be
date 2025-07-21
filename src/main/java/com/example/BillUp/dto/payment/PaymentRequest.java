package com.example.BillUp.dto.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long billId;
    private Long userId;
    private Double amount;
    private String provider;     // e.g., "PAYPAL", "GOOGLE_PAY", "CREDIT_CARD"
    private String methodToken;  // sandbox token or mock identifier
}