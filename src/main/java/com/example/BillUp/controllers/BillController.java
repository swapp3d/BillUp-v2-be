package com.example.BillUp.controllers;

import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.dto.bill.BillResponseDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.Payment;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillType;
import com.example.BillUp.services.BillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<BillResponseDTO> createBill(@Valid @RequestBody BillRequestDTO billRequestDTO) {
        Bill created = billService.createBill(billRequestDTO);
        return ResponseEntity.ok(convertToResponseDTO(created));
    }

    @GetMapping
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillResponseDTO> getBillById(@PathVariable Long id) {
        return billService.getBillById(id)
                .map(bill -> ResponseEntity.ok(convertToResponseDTO(bill)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByUserId(@PathVariable Long userId) {
        List<Bill> bills = billService.getBillsByUserId(userId);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByCompanyId(@PathVariable Long companyId) {
        List<Bill> bills = billService.getBillsByCompanyId(companyId);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByStatus(@PathVariable BillStatus status) {
        List<Bill> bills = billService.getBillsByStatus(status);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByPriority(@PathVariable BillPriority priority) {
        List<Bill> bills = billService.getBillsByPriority(priority);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByType(@PathVariable BillType type) {
        List<Bill> bills = billService.getBillsByType(type);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<BillResponseDTO>> getOverdueBills() {
        List<Bill> bills = billService.getOverdueBills();
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/due-soon")
    public ResponseEntity<List<BillResponseDTO>> getBillsDueSoon(@RequestParam(defaultValue = "7") int days) {
        List<Bill> bills = billService.getBillsDueSoon(days);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/address/{streetAddress}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByStreetAddress(@PathVariable String streetAddress) {
        List<Bill> bills = billService.getBillsByStreetAddress(streetAddress);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillResponseDTO> updateBill(@PathVariable Long id, @RequestBody BillRequestDTO billRequestDTO) {
        Bill updated = billService.updateBill(id, billRequestDTO);
        return ResponseEntity.ok(convertToResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @PostMapping("/{billId}/pay")
    public ResponseEntity<Payment> payBill(
            @PathVariable Long billId,
            @RequestParam Long userId,
            @RequestParam Double amount,
            @RequestParam String provider,
            @RequestParam String methodToken) {

        Payment payment = billService.payBill(billId, userId, amount, provider, methodToken);
        return ResponseEntity.ok(payment);
    }

    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<Double> getTotalAmountByUser(@PathVariable Long userId) {
        Double total = billService.getTotalAmountByUser(userId);
        return ResponseEntity.ok(total);
    }

    @GetMapping("/user/{userId}/unpaid-amount")
    public ResponseEntity<Double> getTotalUnpaidAmountByUser(@PathVariable Long userId) {
        Double total = billService.getTotalUnpaidAmountByUser(userId);
        return ResponseEntity.ok(total);
    }

    @PostMapping("/update-statuses")
    public ResponseEntity<Void> updateAllBillStatuses() {
        billService.updateAllBillStatuses();
        return ResponseEntity.ok().build();
    }

    private BillResponseDTO convertToResponseDTO(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setName(bill.getName());
        dto.setPriority(bill.getPriority());
        dto.setStatus(bill.getStatus());
        dto.setType(bill.getType());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setIssueDate(bill.getIssueDate());
        dto.setCompanyName(bill.getCompany().getName());
        dto.setUserName(bill.getUser().getName() + " " + bill.getUser().getSurname());
        dto.setTotalPaid(bill.getTotalPaid());
        dto.setRemainingAmount(bill.getRemainingAmount());
        return dto;
    }
}