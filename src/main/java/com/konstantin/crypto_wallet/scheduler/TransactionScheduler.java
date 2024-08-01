package com.konstantin.crypto_wallet.scheduler;

import com.konstantin.crypto_wallet.config.Constants;
import com.konstantin.crypto_wallet.service.TransactionService;
import com.konstantin.crypto_wallet.tracker.TransactionTracker;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class TransactionScheduler {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private TransactionTracker transactionTracker;

    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @PostConstruct
    public void startScheduler() {
        taskScheduler.scheduleWithFixedDelay(this::checkPendingTransactions,
                Duration.ofMillis(Constants.TRANSACTIONS_STATUS_CHECK_INTERVAL));
    }

    public void checkPendingTransactions() {
        if (transactionTracker.hasPendingTransactions()) {
            transactionService.checkAndUpdatePendingTransactions();
        }
    }
}
