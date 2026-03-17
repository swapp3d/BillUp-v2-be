package com.example.BillUp.dto.payment;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminPaymentResponseDTO {

    private Long id;
    private Long userId;
    private Long billId;
    private Double amount;
    private LocalDateTime timestamp;
    private String provider;
    private boolean success;
    private String transactionId;
}