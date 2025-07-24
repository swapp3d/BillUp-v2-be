package com.example.BillUp.controllers;

import com.example.BillUp.dto.residence.AddressSuggestion;
import com.example.BillUp.services.AddressService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/autocomplete")
    public List<AddressSuggestion> autocompleteAddress(@RequestParam String query) {
        return addressService.autocompleteAddress(query);
    }
}

