package com.example.BillUp.services;

import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.repositories.BillRepository;
import com.example.BillUp.repositories.CompanyRepository;
import com.example.BillUp.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Transactional
    public Bill createBill(BillRequestDTO dto) {
        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        User user = userRepository.findById(Math.toIntExact(dto.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));

        Bill bill = new Bill();
        bill.setAmount(dto.getAmount());
        bill.setDue_date(dto.getDueDate());
        bill.setType(dto.getType());
        bill.setCompany(company);
        bill.setUser(user);

        // Priority and Status will be auto-calculated by @PrePersist
        return billRepository.save(bill);
    }
}