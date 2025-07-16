package com.example.BillUp.dto.bill;

import lombok.Data;
import com.example.BillUp.enumerators.BillType;
import java.time.LocalDate;

@Data
public class BillRequestDTO {
    private Double amount;
    private LocalDate dueDate;
    private BillType type;
    private Long companyId;
    private Long userId;
}
