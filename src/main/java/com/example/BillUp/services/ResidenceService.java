package com.example.BillUp.services;

import com.example.BillUp.dto.residence.CreateResidenceRequest;
import com.example.BillUp.dto.residence.ResidenceResponse;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.ResidenceType;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResidenceService {

    private final ResidenceRepository residenceRepository;
    private final UserRepository userRepository;
    private final AddressService addressService;

    public ResidenceService(
            ResidenceRepository residenceRepository,
            UserRepository userRepository,
            AddressService addressService
    ) {
        this.residenceRepository = residenceRepository;
        this.userRepository = userRepository;
        this.addressService = addressService;
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

        boolean isValid = addressService.validateAddress(
                request.getStreetAddress(),
                request.getFlatNumber() == null ? "" : request.getFlatNumber(),
                request.getCity(),
                request.getPostalCode(),
                request.getCountry()
        );

        if (!isValid) {
            throw new IllegalArgumentException("Provided address is invalid");
        }

        System.out.println("creating residence");

        Residence res = new Residence();
        System.out.println("residence created");
        res.setUser(user);
        System.out.println("user is set");
        res.setStreetAddress(request.getStreetAddress());
        System.out.println("streetaddrees is set");
        res.setFlatNumber(request.getFlatNumber());
        System.out.println("flatnumber isset");
        res.setCity(request.getCity());
        System.out.println("city isset");
        res.setPostalCode(request.getPostalCode());
        System.out.println("postal code isset");
        res.setCountry(request.getCountry());
        System.out.println("country isset");
        res.setResidenceType(ResidenceType.valueOf(request.getResidenceType()));
        System.out.println("type isset");
        res.setPrimary(request.isPrimary());
        System.out.println("primary isset");
        System.out.println("residence is created");

        Residence saved = residenceRepository.save(res);
        System.out.println("saving the residence" + res);
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
        System.out.println("generating response");
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
        System.out.println("response is set");

        String fullAddress = res.getStreetAddress();
        if (res.getFlatNumber() != null && !res.getFlatNumber().isEmpty()) {
            fullAddress += ", Apt " + res.getFlatNumber();
        }
        dto.setFullAddress(fullAddress);
        System.out.println("full address is set");

        return dto;
    }
}

