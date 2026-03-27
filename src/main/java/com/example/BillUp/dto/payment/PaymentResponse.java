package com.example.BillUp.dto.payment;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private Double amount;
    private String billName;
    private Long billId;
}
