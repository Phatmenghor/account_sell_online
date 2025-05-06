package com.account_sell.feature.order.tasks;

import com.account_sell.feature.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCleanupTask {

    private final OrderService orderService;

    /**
     * Scheduled task to clean up old orders
     * Runs at 01:00 AM every day
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void cleanupOldOrders() {
        log.info("Starting scheduled task for cleaning up old orders");
        try {
            orderService.processOldOrders();
            log.info("Order cleanup task completed successfully");
        } catch (Exception e) {
            log.error("Error during order cleanup task: {}", e.getMessage(), e);
        }
    }
}