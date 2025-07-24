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

    @OneToMany(mappedBy = "bill", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
        System.out.println("Bill status before updatePriorityAndStatus: " + status);

        if (status == BillStatus.PAID || status == BillStatus.FAILED) {
            if (dueDate != null) {
                long daysLeft = LocalDate.now().until(dueDate).getDays();

                if (daysLeft <= 3) {
                    priority = BillPriority.HIGH;
                } else if (daysLeft <= 7) {
                    priority = BillPriority.MEDIUM;
                } else {
                    priority = BillPriority.LOW;
                }
            }
            return;
        }

        if (dueDate != null) {
            long daysLeft = LocalDate.now().until(dueDate).getDays();

            if (daysLeft < 0) {
                status = BillStatus.OVERDUE;
            } else {
                status = BillStatus.OPEN;
            }

            if (daysLeft < 0 || daysLeft <= 3) {
                priority = BillPriority.HIGH;
            } else if (daysLeft <= 7) {
                priority = BillPriority.MEDIUM;
            } else {
                priority = BillPriority.LOW;
            }
        }
    }

    public double getTotalPaid() {
        if (payments == null) return 0.0;
        return payments.stream()
                .filter(Payment::isSuccess)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    public boolean isFullyPaid() {
        return getTotalPaid() >= this.amount;
    }

    public double getRemainingAmount() {
        return this.amount - getTotalPaid();
    }
}
