package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.transaction.Transaction;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionHash(String transactionHash);

    Boolean existsByWalletIdAndStatus(Long walletId, TransactionStatus status);

    Optional<Transaction> findFirstByWalletIdAndStatusOrderByCreatedAtDesc(Long walletId, TransactionStatus status);

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    Boolean existsByTransactionHashAndType(String transactionHash, TransactionType type);
}
