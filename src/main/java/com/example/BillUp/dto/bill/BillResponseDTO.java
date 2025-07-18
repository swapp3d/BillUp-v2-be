package com.example.BillUp.dto.bill;

import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BillResponseDTO {
    private Long id;
    private String name;
    private BillPriority priority;
    private BillStatus status;
    private BillType type;
    private Double amount;
    private LocalDate dueDate;
    private LocalDate issueDate;
    private String companyName;
    private String userName;
    private Double totalPaid;
    private Double remainingAmount;
}