package com.example.BillUp.entities;

import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "bills")
@Where(clause = "deleted = false")
@SQLDelete(sql = "UPDATE bills SET deleted = true WHERE id = ?")
public class Bill {

    @Column(nullable = false)
    private boolean deleted = false;

    @EqualsAndHashCode.Include
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

    public void updatePriorityAndStatus() {
        if (status == BillStatus.PAID || status == BillStatus.FAILED) {
            if (dueDate != null) {
                long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
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
            long daysLeft = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);

            if (daysLeft < 0) {
                status = BillStatus.OVERDUE;
            } else {
                status = BillStatus.OPEN;
            }

            if (daysLeft <= 3) {
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