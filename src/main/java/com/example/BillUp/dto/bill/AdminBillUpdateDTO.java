package com.example.BillUp.dto.bill;

import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminBillUpdateDTO {

    private String name;

    private BillType type;

    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @FutureOrPresent(message = "Due date must be today or in the future")
    private LocalDate dueDate;

    private BillStatus status;

}