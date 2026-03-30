package com.example.BillUp.services;

import com.example.BillUp.dto.residence.*;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.ResidenceType;
import com.example.BillUp.repositories.ResidenceRepository;
import com.example.BillUp.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
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

//    //Admin Tableview
    public List<ResidenceResponse> getAllResidences() {

        return residenceRepository.findAllIncludingDeleted()
                .stream()
                .map(this::toDto)
                .toList();
    }

    //Dashboard
    public List<ResidenceResponse> getDashboardResidences(String street) {

        List<Residence> residences;

        if (street != null && !street.isBlank()) {
            residences = residenceRepository
                    .findByStreetAddressContainingIgnoreCase(street);
        } else {
            residences = residenceRepository.findAllByActiveTrue();
        }

        return residences.stream()
                .map(this::toDto)
                .toList();
    }


    public List<ResidenceResponse> getUserResidences(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return residenceRepository.findByUserId(user.getId())
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

    //Display All Residences of Client (ADMIN)
    public List<ResidenceDropdown> getUserResidencesByUserId(Long userId) {

        User user = userRepository.findByIdIncludingDeleted(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return residenceRepository.findByUserIdIncludingDeleted(user.getId())
                .stream()
                .map(res -> ResidenceDropdown.builder()
                        .id(res.getId())
                        .fullAddress(res.getFullAddress())
                        .build())
                .toList();
    }


    public ResidenceResponse registerResidence(String email, CreateResidenceRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println("creating residence");

        Residence res = new Residence();

        res.setUser(user);
        res.setStreetAddress(request.getStreetAddress());
        res.setFlatNumber(request.getFlatNumber());
        res.setCity(request.getCity());
        res.setPostalCode(request.getPostalCode());
        res.setCountry(request.getCountry());
        res.setResidenceType(ResidenceType.valueOf(request.getResidenceType()));
        res.setPrimary(request.isPrimary());

        Residence saved = residenceRepository.save(res);

        return toDto(saved);

    }

    //Create residence ADMIN

    @Transactional
    public ResidenceResponse adminCreateResidence(AdminCreateResidenceDTO request) {

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Residence res = new Residence();

        res.setUser(user);
        res.setStreetAddress(request.getStreetAddress());
        res.setFlatNumber(request.getFlatNumber());
        res.setCity(request.getCity());
        res.setPostalCode(request.getPostalCode());
        res.setCountry(request.getCountry());
        res.setResidenceType(ResidenceType.valueOf(request.getResidenceType()));
        res.setPrimary(request.isPrimary());

        if (request.isPrimary()) {
            unsetOtherPrimary(user.getId());
        }

        return toDto(residenceRepository.save(res));
    }

    //Updating Residence
    @Transactional
    public ResidenceResponse updateResidence(Long id, UpdateResidenceDTO dto, String email, boolean isAdmin) {

        Residence res = residenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        if (!isAdmin && !res.getUser().getEmail().equals(email)) {
            throw new AccessDeniedException("Unauthorized");
        }

        if (dto.getStreetAddress() != null) res.setStreetAddress(dto.getStreetAddress());
        if (dto.getFlatNumber() != null) res.setFlatNumber(dto.getFlatNumber());
        if (dto.getCity() != null) res.setCity(dto.getCity());
        if (dto.getPostalCode() != null) res.setPostalCode(dto.getPostalCode());
        if (dto.getCountry() != null) res.setCountry(dto.getCountry());

        if (dto.getResidenceType() != null) {
            res.setResidenceType(ResidenceType.valueOf(dto.getResidenceType()));
        }

        if (dto.getIsActive() != null) {
            res.setActive(dto.getIsActive());
        }

        if (dto.getIsPrimary() != null && dto.getIsPrimary()) {
            unsetOtherPrimary(res.getUser().getId());
            res.setPrimary(true);
        }

        return toDto(residenceRepository.save(res));
    }

    //Delete (ADMIN)
    @Transactional
    public void deleteResidence(Long id) {

        Residence res = residenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        res.setDeleted(true);
        res.setActive(false);

        residenceRepository.save(res);
    }

    //Restore (ADMIN)
    @Transactional
    public ResidenceResponse restoreResidence(Long id) {

        Residence res = residenceRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        res.setDeleted(false);
        res.setActive(true);

        return toDto(residenceRepository.save(res));
    }

    //Helper to avoid double primary residences
    private void unsetOtherPrimary(Long userId) {
        List<Residence> residences = residenceRepository.findByUserId(userId);
        residences.forEach(r -> r.setPrimary(false));
        residenceRepository.saveAll(residences);
    }

    //Deactivation
    public void deactivateResidence(Long id, String email) {

        Residence res = residenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        if (!res.getUser().getEmail().equals(email)) {

            throw new AccessDeniedException("Unauthorized");

        }

        res.setActive(false);

        residenceRepository.save(res);

    }

    //Activation
    public void activateResidence(Long id, String email) {

        Residence residence = residenceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        if (!residence.getUser().getEmail().equals(email)) {

            throw new AccessDeniedException("Unauthorized");

        }

        residence.setActive(true);

        residenceRepository.save(residence);

    }

    //Setting Primary
    public void setPrimaryResidence(Long residenceId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<Residence> residences = residenceRepository.findByUserId(user.getId());

        for (Residence residence : residences) {

            residence.setPrimary(residence.getId().equals(residenceId));

        }

        residenceRepository.saveAll(residences);

    }


    @Transactional
    public ResidenceResponse cloneResidence(Long sourceId, String email) {

        Residence src = residenceRepository.findById(sourceId)
                .orElseThrow(() -> new EntityNotFoundException("Residence not found"));

        User me = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Residence copy = new Residence();

        copy.setUser(me);
        copy.setStreetAddress(src.getStreetAddress());
        copy.setFlatNumber(src.getFlatNumber());
        copy.setCity(src.getCity());
        copy.setPostalCode(src.getPostalCode());
        copy.setCountry(src.getCountry());
        copy.setResidenceType(src.getResidenceType());
        copy.setActive(src.isActive());
        copy.setPrimary(true);

        List<Residence> mine = residenceRepository.findByUserId(me.getId());

        mine.forEach(r -> r.setPrimary(false));

        residenceRepository.saveAll(mine);

        Residence saved = residenceRepository.save(copy);

        return toDto(saved);

    }


    public List<ResidenceResponse> autocompleteAddress(String query) {

        return residenceRepository.searchByAddress(query)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());

    }

    public List<ResidenceResponse> searchByStreet(String street) {

        return residenceRepository
                .findByStreetAddressContainingIgnoreCase(street)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private ResidenceResponse toDto(Residence res) {

        ResidenceResponse dto = new ResidenceResponse();

        dto.setId(res.getId());
        dto.setUserId(res.getUser().getId());
        dto.setStreetAddress(res.getStreetAddress());
        dto.setFlatNumber(res.getFlatNumber());
        dto.setCity(res.getCity());
        dto.setPostalCode(res.getPostalCode());
        dto.setCountry(res.getCountry());
        dto.setResidenceType(res.getResidenceType());
        dto.setPrimary(res.isPrimary());
        dto.setActive(res.isActive());
        dto.setDeleted(res.isDeleted());

        String fullAddress = res.getStreetAddress();

        if (res.getFlatNumber() != null && !res.getFlatNumber().isEmpty()) {

            fullAddress += ", Apt " + res.getFlatNumber();

        }

        dto.setFullAddress(fullAddress);

        return dto;

    }

}