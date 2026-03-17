package com.example.BillUp.dto.payment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPaymentUpdateDTO {

    private Boolean success;
    private String provider;
    private LocalDateTime timestamp;
}