package com.example.BillUp.dto.bill;

import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private String streetAddress;
    private Double totalPaid;
    private Double remainingAmount;
}