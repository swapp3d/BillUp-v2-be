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

import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "bills")
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDate issueDate;

    @Column(nullable = false)
    private String streetAddress;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Payment> payments;

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL)
    private List<Notification> notifications;

    @PrePersist
    public void onPrePersist() {
        if (issueDate == null) {
            issueDate = LocalDate.now();
        }
        if (status == null) {
            status = BillStatus.OPEN;
        }
        updatePriorityAndStatus();
    }

    @PreUpdate
    public void onPreUpdate() {
        updatePriorityAndStatus();
    }

    private void updatePriorityAndStatus() {
        LocalDate today = LocalDate.now();

        if (dueDate != null) {
            long daysLeft = today.until(dueDate).getDays();

            // Update status based on due date
            if (daysLeft < 0 && status != BillStatus.PAID) {
                status = BillStatus.OVERDUE;
            }

            // Update priority based on days left
            if (daysLeft < 0) {
                priority = BillPriority.HIGH;
            } else if (daysLeft <= 3) {
                priority = BillPriority.HIGH;
            } else if (daysLeft <= 7) {
                priority = BillPriority.MEDIUM;
            } else {
                priority = BillPriority.LOW;
            }

            // Keep OPEN status if not overdue and not paid/failed
            if (status != BillStatus.PAID && status != BillStatus.FAILED && daysLeft >= 0) {
                status = BillStatus.OPEN;
            }
        }
    }

    public Double getTotalPaid() {
        return payments != null ?
                payments.stream().mapToDouble(Payment::getAmount).sum() : 0.0;
    }

    public Double getRemainingAmount() {
        return amount - getTotalPaid();
    }

    public boolean isFullyPaid() {
        return getTotalPaid() >= amount;
    }
}
