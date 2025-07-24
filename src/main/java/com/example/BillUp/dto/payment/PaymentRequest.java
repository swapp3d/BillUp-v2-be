package com.example.BillUp.dto.payment;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class PaymentRequest {

    @NotNull(message = "Bill ID is required")
    @Positive(message = "Bill ID must be positive")
    private Long billId;

    @NotNull(message = "User ID is required")
    @Positive(message = "User ID must be positive")
    private Long userId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotBlank(message = "Provider is required")
    private String provider;

    @NotBlank(message = "Payment method token is required")
    private String methodToken;
}
