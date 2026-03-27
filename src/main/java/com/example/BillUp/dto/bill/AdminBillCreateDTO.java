package com.example.BillUp.dto.bill;

import com.example.BillUp.enumerators.BillType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBillCreateDTO {

    @NotNull(message = "Company id is required")
    private Long companyId;

    @NotNull(message = "Residence id is required")
    private Long residenceId;

    @NotBlank(message = "Bill name is required")
    private String name;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @NotNull(message = "Due date is required")
    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    @NotNull(message = "Bill type is required")
    private BillType type;
}