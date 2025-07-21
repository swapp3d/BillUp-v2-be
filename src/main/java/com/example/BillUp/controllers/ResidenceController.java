package com.example.BillUp.controllers;

import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.services.ResidenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/residences")
public class ResidenceController {

    private final ResidenceService residenceService;
    public ResidenceController(ResidenceService residenceService) {
        this.residenceService = residenceService;
    }


    @GetMapping
    public List<ResidenceResponse> getMyResidences(Principal principal) {
        return residenceService.getUserResidences(principal.getName());
    }

    @PostMapping
    public ResponseEntity<ResidenceResponse> registerResidence(
            @Valid @RequestBody CreateResidenceRequest request,
            Principal principal
    ) {
        ResidenceResponse response = residenceService.registerResidence(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}/set-primary")
    public ResponseEntity<?> setPrimary(@PathVariable Long id, Principal principal) {
        residenceService.setPrimaryResidence(id, principal.getName());
        return ResponseEntity.ok("Primary residence updated.");
    }


    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateResidence(@PathVariable Long id, Principal principal) {
        residenceService.deactivateResidence(id, principal.getName());
        return ResponseEntity.ok("Residence deactivated");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @GetMapping("/autocomplete")
    public List<ResidenceResponse> autocompleteAddress(@RequestParam String query) {
        return residenceService.autocompleteAddress(query);
    }
}

