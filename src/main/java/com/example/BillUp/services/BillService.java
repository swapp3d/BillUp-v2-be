package com.example.BillUp.services;

import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.Company;
import com.example.BillUp.entities.User;
import com.example.BillUp.entities.Residence;
import com.example.BillUp.entities.Payment;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillType;
import com.example.BillUp.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillService {

    private final BillRepository billRepository;
    private final CompanyRepository companyRepository;
    private final ResidenceRepository residenceRepository;
    private final PaymentService paymentService;

    @Transactional
    public Bill createBill(BillRequestDTO dto) {
        Company company = companyRepository.findById(Math.toIntExact(dto.getCompanyId()))
                .orElseThrow(() -> new RuntimeException("Company not found"));

        Residence residence = residenceRepository.findById(dto.getResidenceId())
                .orElseThrow(() -> new RuntimeException("Residence not found"));

        User user = residence.getUser();

        Bill bill = Bill.builder()
                .name(dto.getName())
                .amount(dto.getAmount())
                .dueDate(dto.getDueDate())
                .type(dto.getType())
                .company(company)
                .user(user)
                .streetAddress(residence.getStreetAddress())
                .issueDate(LocalDate.now())
                .status(BillStatus.OPEN)
                .build();

        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Optional<Bill> getBillById(Long id) {
        return billRepository.findById(id);
    }

    public List<Bill> getBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

    public List<Bill> getBillsByCompanyId(Long companyId) {
        return billRepository.findByCompanyId(companyId);
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
    public Bill updateBill(Long id, BillRequestDTO dto) {
        Bill bill = billRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        if (dto.getName() != null) bill.setName(dto.getName());
        if (dto.getAmount() != null) bill.setAmount(dto.getAmount());
        if (dto.getDueDate() != null) bill.setDueDate(dto.getDueDate());
        if (dto.getType() != null) bill.setType(dto.getType());

        return billRepository.save(bill);
    }

    @Transactional
    public void deleteBill(Long id) {
        if (!billRepository.existsById(id)) {
            throw new RuntimeException("Bill not found");
        }
        billRepository.deleteById(id);
    }


    @Transactional
    public Payment payBill(Long billId, Long userId, Double amount, String provider, String methodToken) {
        return paymentService.processBillPayment(billId, userId, amount, provider, methodToken);
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