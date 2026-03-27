package com.example.BillUp.services;

import com.example.BillUp.dto.bill.AdminBillCreateDTO;
import com.example.BillUp.dto.bill.AdminBillUpdateDTO;
import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillType;
import com.example.BillUp.repositories.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final CompanyRepository companyRepository;
    private final ResidenceRepository residenceRepository;
    private final PaymentService paymentService;

    private boolean isOwnerOrAdmin(Bill bill, User user) {

        if (user.getRole().name().equals("ADMIN")) {
            return true;
        }

        return bill.getCompany().getUser().getId().equals(user.getId());
    }

    @Transactional
    public Bill createBill(Long residenceId, BillRequestDTO dto, User currentUser) {

        Company company = companyRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Residence residence = residenceRepository.findById(residenceId)
                .orElseThrow(() -> new RuntimeException("Residence not found"));

        Bill bill = Bill.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .dueDate(dto.getDueDate())
                .type(dto.getType())
                .company(company)
                .user(residence.getUser())
                .streetAddress(residence.getStreetAddress())
                .build();

        return billRepository.save(bill);
    }

    @Transactional
    public Bill adminCreateBill(AdminBillCreateDTO dto) {

        Company company = companyRepository.findById(dto.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Residence residence = residenceRepository.findById(dto.getResidenceId())
                .orElseThrow(() -> new RuntimeException("Residence not found"));

        Bill bill = Bill.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .dueDate(dto.getDueDate())
                .type(dto.getType())
                .company(company)
                .user(residence.getUser())
                .streetAddress(residence.getStreetAddress())
                .build();

        return billRepository.save(bill);
    }

    public List<Bill> getMyBills(User currentUser) {

        if (currentUser.getRole().name().equals("ADMIN")) {
            return billRepository.findAllIncludingDeleted();
        }

        if (currentUser.getRole().name().equals("CLIENT")) {
            return billRepository.findByUserId(currentUser.getId());
        }

        Company company = companyRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        return billRepository.findByCompanyId(company.getId());
    }




    public List<Bill> getAllBills() {
        return billRepository.findAllIncludingDeleted();
    }

    public List<Bill> getBillsByStatus(BillStatus status) {
        return billRepository.findByStatus(status);
    }

    public List<Bill> getBillsByPriority(BillPriority priority) {
        return billRepository.findByPriority(priority);
    }

    public List<Bill> getBillsByType(BillType type) {
        return billRepository.findByType(type);
    }

    public List<Bill> getOverdueBills() {
        return billRepository.findByStatus(BillStatus.OVERDUE);
    }

    public List<Bill> getBillsDueSoon(int days) {
        LocalDate cutoffDate = LocalDate.now().plusDays(days);
        return billRepository.findByDueDateBeforeAndStatus(cutoffDate, BillStatus.OPEN);
    }

    @Transactional
    public Bill updateBill(Long id, BillRequestDTO dto, User currentUser) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!isOwnerOrAdmin(bill, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        if (dto.getName() != null) {
            bill.setName(dto.getName());
        }

        if (dto.getType() != null) {
            bill.setType(dto.getType());
        }

        if (dto.getDueDate() != null) {
            bill.setDueDate(dto.getDueDate());
        }

        return billRepository.save(bill);
    }

    @Transactional
    public Bill adminUpdateBill(Long id, AdminBillUpdateDTO dto, User currentUser) {

        if (!currentUser.getRole().name().equals("ADMIN")) {
            throw new RuntimeException("Only admin can update bill with extended permissions");
        }

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (dto.getName() != null) {
            bill.setName(dto.getName());
        }

        if (dto.getType() != null) {
            bill.setType(dto.getType());
        }

        if (dto.getAmount() != null) {
            bill.setAmount(dto.getAmount());
        }

        if (dto.getDueDate() != null) {
            bill.setDueDate(dto.getDueDate());
        }

        if (dto.getStatus() != null) {

            if (dto.getStatus() == BillStatus.PAID || dto.getStatus() == BillStatus.FAILED) {
                bill.setStatus(dto.getStatus());
            } else {
                throw new RuntimeException("Status can only be manually set to PAID or FAILED");
            }

        }

        return billRepository.save(bill);
    }

    @Transactional
    public void deleteBill(Long id, User currentUser) {

        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (!isOwnerOrAdmin(bill, currentUser)) {
            throw new RuntimeException("Access denied");
        }

        billRepository.deleteById(id);
    }

    @Transactional
    public Bill restoreBill(Long id) {

        Bill bill = billRepository.findByIdIncludingDeleted(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        bill.setDeleted(false);
        return billRepository.save(bill);
    }


    @Transactional
    public void updateAllBillStatuses() {
        List<Bill> openBills = billRepository.findByStatus(BillStatus.OPEN);
        LocalDate today = LocalDate.now();

        for (Bill bill : openBills) {
            if (bill.getDueDate().isBefore(today)) {
                bill.setStatus(BillStatus.OVERDUE);
                billRepository.save(bill);
            }
        }
    }

    public Double getTotalAmountByUser(Long userId) {
        return billRepository.findByUserId(userId).stream()
                .mapToDouble(Bill::getAmount)
                .sum();
    }

    public Double getTotalUnpaidAmountByUser(Long userId) {
        return billRepository.findByUserIdAndStatusNot(userId, BillStatus.PAID).stream()
                .mapToDouble(Bill::getRemainingAmount)
                .sum();
    }

    public List<Bill> getBillsByStreetAddress(String streetAddress) {
        return billRepository.findByStreetAddress(streetAddress);
    }
}