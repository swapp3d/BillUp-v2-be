package com.example.BillUp.dto.authentication;

import com.example.BillUp.entities.Token;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    @JsonProperty("access_token")
    public String token;
}
