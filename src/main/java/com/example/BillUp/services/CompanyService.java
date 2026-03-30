package com.example.BillUp.services;

import com.example.BillUp.dto.company.CompanyResponseDTO;
import com.example.BillUp.dto.company.UpdateCompanyDTO;
import com.example.BillUp.entities.Company;
import com.example.BillUp.repositories.CompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;

    public List<CompanyResponseDTO> getAllCompanies() {
        return companyRepository.findAllIncludingDeleted()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public CompanyResponseDTO updateCompany(Long id, UpdateCompanyDTO dto) {
        Company company = companyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            company.setName(dto.getName());
        }
        if (dto.getCompanyEmail() != null && !dto.getCompanyEmail().isBlank()) {
            company.setCompanyEmail(dto.getCompanyEmail());
        }
        if (dto.getCompanyNumber() != null && !dto.getCompanyNumber().isBlank()) {
            company.setCompanyNumber(dto.getCompanyNumber());
        }

        return toDto(companyRepository.save(company));
    }

    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsByIdNative(id)) {
            throw new EntityNotFoundException("Company not found");
        }
        companyRepository.softDeleteById(id);
    }

    @Transactional
    public CompanyResponseDTO restoreCompany(Long id) {
        Company company = companyRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found"));
        company.setDeleted(false);
        return toDto(companyRepository.save(company));
    }

    private CompanyResponseDTO toDto(Company c) {
        return CompanyResponseDTO.builder()
                .id(c.getId())
                .userId(c.getUser() != null ? c.getUser().getId() : null)
                .name(c.getName())
                .companyEmail(c.getCompanyEmail())
                .companyNumber(c.getCompanyNumber())
                .deleted(c.isDeleted())
                .build();
    }
}