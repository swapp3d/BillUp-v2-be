package com.example.BillUp.dto.payment;

import lombok.Data;

@Data
public class PaymentRequest {
    private Long billId;
    private Long userId;
    private Double amount;
    private String provider;
    private String methodToken;
}