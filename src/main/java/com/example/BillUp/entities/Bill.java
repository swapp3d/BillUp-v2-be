package com.example.BillUp.entities;

import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BillPriority priority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BillStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BillType type;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDate due_date;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bill")
    private List<Payment> payments;

    @OneToMany(mappedBy = "bill")
    private List<Notification> notifications;

    @PrePersist
    @PreUpdate
    public void updatePriorityAndStatus() {
        LocalDate today = LocalDate.now();

        // Assign priority
        if (due_date != null) {
            long daysLeft = today.until(due_date).getDays();

            if (daysLeft < 0) {
                status = BillStatus.OVERDUE;
                priority = BillPriority.HIGH;
            } else if (daysLeft <= 3) {
                priority = BillPriority.HIGH;
            } else if (daysLeft <= 7) {
                priority = BillPriority.MEDIUM;
            } else {
                priority = BillPriority.LOW;
            }

            // Default to OPEN if not overdue and not paid/failed yet
            if (status == null || status == BillStatus.OPEN) {
                status = BillStatus.OPEN;
            }
        }
    }

}
