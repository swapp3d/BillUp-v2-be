package com.example.BillUp.services;

import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ResidenceService {

    private final ResidenceRepository residenceRepository;
    private final UserRepository userRepository;

    public ResidenceService(ResidenceRepository residenceRepository, UserRepository userRepository) {
        this.residenceRepository = residenceRepository;
        this.userRepository = userRepository;
    }

    public List<ResidenceResponse> getUserResidences(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return residenceRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ResidenceResponse registerResidence(String email, CreateResidenceRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Residence res = new Residence();
        res.setUser(user);
        res.setStreetAddress(request.getStreetAddress());
        res.setFlatNumber(request.getFlatNumber());
        res.setCity(request.getCity());
        res.setPostalCode(request.getPostalCode());
        res.setCountry(request.getCountry());
        res.setResidenceType(request.getResidenceType());
        res.setPrimary(request.isPrimary());

        Residence saved = residenceRepository.save(res);
        return toDto(saved);
    }

    public void deactivateResidence(Long id, String username) {
        Residence res = residenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        if (!res.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Unauthorized");
        }

        res.setActive(false);
        residenceRepository.save(res);
    }

    public void setPrimaryResidence(Long residenceId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Residence> residences = residenceRepository.findByUserId(user.getId());

        for (Residence residence : residences) {
            residence.setPrimary(residence.getId().equals(residenceId));
        }

        residenceRepository.saveAll(residences);
    }


    public List<ResidenceResponse> autocompleteAddress(String query) {
        return residenceRepository.searchByAddress(query)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private ResidenceResponse toDto(Residence res) {
        ResidenceResponse dto = new ResidenceResponse();
        dto.setId(res.getId());
        dto.setStreetAddress(res.getStreetAddress());
        dto.setFlatNumber(res.getFlatNumber());
        dto.setCity(res.getCity());
        dto.setPostalCode(res.getPostalCode());
        dto.setCountry(res.getCountry());
        dto.setResidenceType(res.getResidenceType());
        dto.setPrimary(res.isPrimary());
        dto.setActive(res.isActive());

        String fullAddress = res.getStreetAddress();
        if (res.getFlatNumber() != null && !res.getFlatNumber().isEmpty()) {
            fullAddress += ", Apt " + res.getFlatNumber();
        }
        dto.setFullAddress(fullAddress);

        return dto;
    }

}

