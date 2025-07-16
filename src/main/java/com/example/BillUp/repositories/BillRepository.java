package com.example.BillUp.repositories;

import com.example.BillUp.entities.Bill;
import com.example.BillUp.enumerators.BillStatus;
import com.example.BillUp.enumerators.BillPriority;
import com.example.BillUp.enumerators.BillType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BillRepository extends JpaRepository<Bill, Long> {

    List<Bill> findByUserId(Long userId);

    List<Bill> findByCompanyId(Long companyId);

    List<Bill> findByStatus(BillStatus status);

    List<Bill> findByPriority(BillPriority priority);

    List<Bill> findByType(BillType type);

    List<Bill> findByUserIdAndStatus(Long userId, BillStatus status);

    List<Bill> findByUserIdAndStatusNot(Long userId, BillStatus status);

    List<Bill> findByDueDateBeforeAndStatus(LocalDate date, BillStatus status);

    List<Bill> findByDueDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Bill b WHERE b.user.id = :userId AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Bill> findByUserIdAndDueDateBetween(@Param("userId") Long userId,
                                             @Param("startDate") LocalDate startDate,
                                             @Param("endDate") LocalDate endDate);

    @Query("SELECT b FROM Bill b WHERE b.company.id = :companyId AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Bill> findByCompanyIdAndDueDateBetween(@Param("companyId") Long companyId,
                                                @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);
}