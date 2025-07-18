package com.example.BillUp.dto.residence;

import lombok.Data;

import java.util.Map;

@Data
public class AddressSuggestion {
    private String display_name;
    private Map<String, String> address;
}


