package com.example.BillUp.services;

import com.example.BillUp.dto.residence.AddressSuggestion;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AddressService {

    private final List<AddressSuggestion> mockSuggestions = List.of(
            buildSuggestion("Gedimino pr. 9-19, Vilnius, LT-01103, Lithuania",
                    "Gedimino pr. 9", "19", "Vilnius", "LT-01103", "Lithuania"),
            buildSuggestion("Antakalnio g. 12, Vilnius, LT-10232, Lithuania",
                    "Antakalnio g. 12", "", "Vilnius", "LT-10232", "Lithuania")
    );

    private AddressSuggestion buildSuggestion(String displayName, String streetAddress,
                                              String flatNumber, String city,
                                              String postcode, String country) {
        AddressSuggestion suggestion = new AddressSuggestion();
        suggestion.setDisplay_name(displayName);
        suggestion.setAddress(Map.of(
                "street_address", streetAddress,
                "flat_number", flatNumber,
                "city", city,
                "postcode", postcode,
                "country", country
        ));
        return suggestion;
    }

    public boolean validateAddress(String streetAddress, String flatNumber,
                                   String city, String postalCode, String country) {
        return mockSuggestions.stream().anyMatch(s -> {
            Map<String, String> addr = s.getAddress();
            return streetAddress.trim().equalsIgnoreCase(addr.get("street_address"))
                    && flatNumber.trim().equalsIgnoreCase(addr.get("flat_number"))
                    && city.trim().equalsIgnoreCase(addr.get("city"))
                    && postalCode.trim().equalsIgnoreCase(addr.get("postcode"))
                    && country.trim().equalsIgnoreCase(addr.get("country"));
        });
    }

    public List<AddressSuggestion> autocompleteAddress(String query) {
        String q = query.toLowerCase();
        return mockSuggestions.stream()
                .filter(s -> s.getDisplay_name().toLowerCase().contains(q))
                .toList();
    }
}
