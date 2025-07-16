package com.example.BillUp.controllers;

import com.example.BillUp.dto.bill.BillRequestDTO;
import com.example.BillUp.entities.Bill;
import com.example.BillUp.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bills")
@RequiredArgsConstructor
public class BillController {

    private final BillService billService;

    @PostMapping
    public ResponseEntity<Bill> createBill(@RequestBody BillRequestDTO billRequestDTO) {
        Bill created = billService.createBill(billRequestDTO);
        return ResponseEntity.ok(created);
    }
}
