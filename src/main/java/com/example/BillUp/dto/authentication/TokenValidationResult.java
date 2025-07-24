package com.example.BillUp.dto.authentication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResult {
    boolean isValid;
    boolean isRevoked;
    boolean isExpired;
    String email;
}
