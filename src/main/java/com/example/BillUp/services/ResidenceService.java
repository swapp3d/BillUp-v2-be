package com.example.BillUp.services;

import com.example.BillUp.dto.CreateResidenceRequest;
import com.example.BillUp.dto.ResidenceResponse;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.repositories.ResidenceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ResidenceService {

    @Autowired
    private ResidenceRepository residenceRepository;

    @Autowired
    private UserRepository userRepository; //will be created

    public List<ResidenceResponse> getUserResidences(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return residenceRepository.findByUserId(user.getId()).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ResidenceResponse registerResidence(String username, CreateResidenceRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Residence res = new Residence();
        res.setUser(user);
        res.setAddress(request.getAddress());
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

    public List<ResidenceResponse> autocompleteAddress(String query) {
        return residenceRepository.searchByAddress(query)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private ResidenceResponse toDto(Residence res) {
        ResidenceResponse dto = new ResidenceResponse();
        dto.setId(res.getId());
        dto.setAddress(res.getAddress());
        dto.setCity(res.getCity());
        dto.setCountry(res.getCountry());
        dto.setPrimary(res.isPrimary());
        dto.setActive(res.isActive());
        return dto;
    }
}

