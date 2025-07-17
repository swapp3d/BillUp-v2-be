package com.example.BillUp.dto.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    @JsonProperty("access_token")
    private String token;
}
