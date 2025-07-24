package com.example.BillUp.dto.bill;

import com.example.BillUp.enumerators.BillStatus;
import lombok.Data;
import com.example.BillUp.enumerators.BillType;
import java.time.LocalDate;

@Data
public class BillRequestDTO {
    private String name;
    private Double amount;
    private BillStatus status;
    private LocalDate dueDate;
    private BillType type;
    private Long companyId;
    private Long residenceId;
}
