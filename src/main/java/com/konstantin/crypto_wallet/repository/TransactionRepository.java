package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.transaction.Transaction;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionHash(String transactionHash);

    Boolean existsByWalletIdAndStatus(Long walletId, TransactionStatus status);

    Optional<Transaction> findFirstByWalletIdAndStatusOrderByCreatedAtDesc(Long walletId, TransactionStatus status);

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);
}
