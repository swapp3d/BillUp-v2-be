package com.example.BillUp.dto.authentication;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {
    boolean isValid;
    boolean isRevoked;
    boolean isExpired;
    String email;
}
