package com.example.BillUp.controller;

import com.example.BillUp.dto.CreateResidenceRequest;
import com.example.BillUp.dto.ResidenceResponse;
import com.example.BillUp.service.ResidenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/residences")
public class ResidenceController {

    @Autowired
    private ResidenceService residenceService;

    @GetMapping
    public List<ResidenceResponse> getMyResidences(Principal principal) {
        return residenceService.getUserResidences(principal.getName());
    }

    @PostMapping
    public ResponseEntity<ResidenceResponse> registerResidence(
            @RequestBody CreateResidenceRequest request,
            Principal principal) {
        ResidenceResponse res = residenceService.registerResidence(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateResidence(@PathVariable Long id, Principal principal) {
        residenceService.deactivateResidence(id, principal.getName());
        return ResponseEntity.ok("Residence deactivated");
    }

    @GetMapping("/autocomplete")
    public List<ResidenceResponse> autocomplete(@RequestParam String query) {
        return residenceService.autocompleteAddress(query);
    }
}

