package com.example.BillUp.dto.payment;

import com.example.BillUp.enumerators.BillStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private Double amount;
    private String billName;
    private Long billId;
}
