package com.example.BillUp.controllers;

import com.example.BillUp.dto.bill.AdminBillCreateDTO;
import com.example.BillUp.dto.bill.AdminBillUpdateDTO;
import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.dto.bill.BillResponseDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.entities.User;
import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import com.example.BillUp.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/v1/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    //CREATE BILL (COMPANY)
    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/residence/{residenceId}")
    public ResponseEntity<BillResponseDTO> createBill(
            @PathVariable Long residenceId,
            @RequestBody BillRequestDTO dto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Bill created = billService.createBill(residenceId, dto, currentUser);
        return ResponseEntity.ok(convert(created));
    }

    //CREATE BILL (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<BillResponseDTO> adminCreateBill(
            @RequestBody AdminBillCreateDTO dto) {

        Bill created = billService.adminCreateBill(dto);

        return ResponseEntity.ok(convert(created));
    }

    //GET MY BILLS
    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<BillResponseDTO>> getMyBills(Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        List<Bill> bills = billService.getMyBills(currentUser);

        return ResponseEntity.ok(
                bills.stream().map(this::convert).collect(toList())
        );
    }

    //ADMIN: GET ALL
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BillResponseDTO>> getAllBills() {

        List<Bill> bills = billService.getAllBills();

        return ResponseEntity.ok(
                bills.stream().map(this::convert).collect(toList())
        );
    }

    //UPDATE BILL
    @PreAuthorize("hasAnyRole('COMPANY','ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<BillResponseDTO> updateBill(
            @PathVariable Long id,
            @RequestBody BillRequestDTO dto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        Bill updated = billService.updateBill(id, dto, currentUser);

        return ResponseEntity.ok(convert(updated));
    }

    //UPDATE BILL ADMIN

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{id}")
    public ResponseEntity<BillResponseDTO> adminUpdateBill(
            @PathVariable Long id,
            @RequestBody AdminBillUpdateDTO dto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();

        Bill updated = billService.adminUpdateBill(id, dto, currentUser);

        return ResponseEntity.ok(convert(updated));
    }

    //DELETE BILL (SOFT)
    @PreAuthorize("hasAnyRole('COMPANY','ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(
            @PathVariable Long id,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        billService.deleteBill(id, currentUser);

        return ResponseEntity.noContent().build();
    }

    //RESTORE BILL (ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}/restore")
    public ResponseEntity<BillResponseDTO> restoreBill(@PathVariable Long id) {

        Bill restored = billService.restoreBill(id);
        return ResponseEntity.ok(convert(restored));
    }

    //FILTERING

    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByStatus(@PathVariable BillStatus status) {
        List<Bill> bills = billService.getBillsByStatus(status);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByPriority(@PathVariable BillPriority priority) {
        List<Bill> bills = billService.getBillsByPriority(priority);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }


    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByType(@PathVariable BillType type) {
        List<Bill> bills = billService.getBillsByType(type);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/overdue")
    public ResponseEntity<List<BillResponseDTO>> getOverdueBills() {
        List<Bill> bills = billService.getOverdueBills();
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('CLIENT','COMPANY','ADMIN')")
    @GetMapping("/due-soon")
    public ResponseEntity<List<BillResponseDTO>> getBillsDueSoon(@RequestParam(defaultValue = "7") int days) {
        List<Bill> bills = billService.getBillsDueSoon(days);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('COMPANY','ADMIN')")
    @GetMapping("/address/{streetAddress}")
    public ResponseEntity<List<BillResponseDTO>> getBillsByStreetAddress(@PathVariable String streetAddress) {
        List<Bill> bills = billService.getBillsByStreetAddress(streetAddress);
        List<BillResponseDTO> response = bills.stream()
                .map(this::convert).toList();
        return ResponseEntity.ok(response);
    }
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @GetMapping("/user/{userId}/total-amount")
    public ResponseEntity<Double> getTotalAmountByUser(@PathVariable Long userId) {
        Double total = billService.getTotalAmountByUser(userId);
        return ResponseEntity.ok(total);
    }
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @GetMapping("/user/{userId}/unpaid-amount")
    public ResponseEntity<Double> getTotalUnpaidAmountByUser(@PathVariable Long userId) {
        Double total = billService.getTotalUnpaidAmountByUser(userId);
        return ResponseEntity.ok(total);
    }
    @PreAuthorize("hasAnyRole('CLIENT','ADMIN')")
    @PostMapping("/update-statuses")
    public ResponseEntity<Void> updateAllBillStatuses() {
        billService.updateAllBillStatuses();
        return ResponseEntity.ok().build();
    }

    private BillResponseDTO convert(Bill bill) {
        BillResponseDTO dto = new BillResponseDTO();
        dto.setId(bill.getId());
        dto.setName(bill.getName());
        dto.setPriority(bill.getPriority());
        dto.setStatus(bill.getStatus());
        dto.setType(bill.getType());
        dto.setAmount(bill.getAmount());
        dto.setDueDate(bill.getDueDate());
        dto.setIssueDate(bill.getIssueDate());
        dto.setCompanyName(bill.getCompany() != null ? bill.getCompany().getName() : null);
        dto.setUserName(bill.getUser() != null
                ? bill.getUser().getName() + " " + bill.getUser().getSurname()
                : null);
        dto.setStreetAddress(bill.getStreetAddress());
        dto.setTotalPaid(bill.getTotalPaid());
        dto.setRemainingAmount(bill.getRemainingAmount());
        dto.setDeleted(bill.isDeleted());
        return dto;
    }
}