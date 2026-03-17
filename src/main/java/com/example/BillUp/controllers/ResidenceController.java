package com.example.BillUp.controllers;

import com.example.BillUp.dto.residence.AdminCreateResidenceDTO;
import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.dto.residence.UpdateResidenceDTO;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.services.ResidenceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/residences")
public class ResidenceController {

    private final ResidenceService residenceService;
    public ResidenceController(ResidenceService residenceService) {
        this.residenceService = residenceService;
    }


    //Company Dashboard getting all active residences
    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/company/dashboard")
    public ResponseEntity<List<ResidenceResponse>> getDashboardResidences(
            @RequestParam(required = false) String street) {

        return ResponseEntity.ok(
                residenceService.getDashboardResidences(street)
        );
    }

    //Residence Display (CLIENT)
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public List<ResidenceResponse> getMyResidences(Principal principal) {
        return residenceService.getUserResidences(principal.getName());
    }

    //Initial Residence Registration
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PostMapping
    public ResponseEntity<ResidenceResponse> registerResidence(
            @Valid @RequestBody CreateResidenceRequest request,
            Principal principal) {
        ResidenceResponse response = residenceService.registerResidence(principal.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //Creating Residence (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<ResidenceResponse> adminCreateResidence(
            @Valid @RequestBody AdminCreateResidenceDTO request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(residenceService.adminCreateResidence(request));
    }

    //Editing of Residences
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ResidenceResponse> updateResidence(
            @PathVariable Long id,
            @RequestBody UpdateResidenceDTO dto,
            Principal principal,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return ResponseEntity.ok(
                residenceService.updateResidence(id, dto, principal.getName(), isAdmin)
        );
    }

    //Deleting (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResidence(@PathVariable Long id) {

        residenceService.deleteResidence(id);
        return ResponseEntity.noContent().build();
    }

    //Restoring (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<ResidenceResponse> restoreResidence(@PathVariable Long id) {

        return ResponseEntity.ok(residenceService.restoreResidence(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{id}/set-primary")
    public ResponseEntity<?> setPrimary(@PathVariable Long id, Principal principal) {
        residenceService.setPrimaryResidence(id, principal.getName());
        return ResponseEntity.ok("Primary residence updated.");
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PostMapping("/{id}/clone")
    public ResidenceResponse cloneResidence(
            @PathVariable Long id,
            Principal principal
    ) {
        return residenceService.cloneResidence(id, principal.getName());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateResidence(@PathVariable Long id, Principal principal) {
        residenceService.deactivateResidence(id, principal.getName());
        return ResponseEntity.ok("Residence deactivated");
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    @PutMapping("/{id}/activate")
    public ResponseEntity<?> activateResidence(@PathVariable Long id, Principal principal) {
        residenceService.activateResidence(id, principal.getName());
        return ResponseEntity.ok("Residence activated");
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


