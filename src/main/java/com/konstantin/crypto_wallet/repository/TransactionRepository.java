package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByWalletId(Long walletId);

    Optional<Transaction> findByTransactionHash(String transactionHash);
}
