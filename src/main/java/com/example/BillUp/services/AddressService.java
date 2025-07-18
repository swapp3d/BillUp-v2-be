package com.example.BillUp.services;

import com.example.BillUp.dto.residence.AddressSuggestion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
public class AddressService {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<AddressSuggestion> autocompleteAddress(String query) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder
                .fromHttpUrl(NOMINATIM_URL)
                .queryParam("q", query)
                .queryParam("format", "json")
                .queryParam("addressdetails", 1)
                .queryParam("limit", 5)
                .build()
                .toUri();

        String response = restTemplate.getForObject(uri, String.class);

        try {
            return objectMapper.readValue(response, new TypeReference<List<AddressSuggestion>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse address suggestions", e);
        }
    }
}

