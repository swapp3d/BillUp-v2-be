package com.example.BillUp.dto.residence;

import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressSuggestion {
    private String display_name;
    private Map<String, String> address;
}


