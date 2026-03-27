package com.example.BillUp.dto.payment;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPaymentUpdateDTO {

    private Boolean success;
    private String provider;
    private LocalDateTime timestamp;
}