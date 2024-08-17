package com.konstantin.crypto_wallet.tracker;

import com.konstantin.crypto_wallet.model.transaction.Transaction;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class TransactionTracker {

    private final Set<Transaction> pendingTransactions = Collections.synchronizedSet(new HashSet<>());

    public void addTransaction(Transaction transaction) {
        pendingTransactions.add(transaction);
    }

    public void removeTransaction(Transaction transaction) {
        pendingTransactions.remove(transaction);
    }

    public Set<Transaction> getPendingTransactions() {
        return Collections.unmodifiableSet(pendingTransactions);
    }

    public boolean hasPendingTransactions() {
        return !pendingTransactions.isEmpty();
    }
}
