package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.tracker.PendingTransactionTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;

@Service
@RequiredArgsConstructor
public class TransactionStatusService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private Web3j web3j;

    @Autowired
    private PendingTransactionTracker pendingTransactionTracker;

    public void checkAndUpdatePendingTransactions() {
        var pendingTransactions = pendingTransactionTracker.getPendingTransactions();

        pendingTransactions.forEach(transaction -> {
            var receiptOpt = web3j.ethGetTransactionReceipt(transaction.getTransactionHash())
                    .sendAsync().join().getTransactionReceipt();

            receiptOpt.ifPresent(receipt -> {
                var newStatus = receipt.isStatusOK() ? TransactionStatus.COMPLETED : TransactionStatus.FAILED;
                transaction.setStatus(newStatus);
                transactionRepository.save(transaction);
                pendingTransactionTracker.removeTransaction(transaction);
            });
        });
    }
}
