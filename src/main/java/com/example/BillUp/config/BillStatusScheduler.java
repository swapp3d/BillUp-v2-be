package com.example.BillUp.config;

import com.example.BillUp.services.BillService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BillStatusScheduler {

    private final BillService billService;

    // Run every day at midnight to update bill statuses
    @Scheduled(cron = "0 0 0 * * *")
    public void updateBillStatuses() {
        billService.updateAllBillStatuses();
    }
}