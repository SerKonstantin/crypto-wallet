package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.transaction.Transaction;
import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionHashAndType(String transactionHash, TransactionType type);

    Boolean existsByWalletIdAndStatus(Long walletId, TransactionStatus status);

    Optional<Transaction> findFirstByWalletIdAndStatusOrderByCreatedAtDesc(Long walletId, TransactionStatus status);

    List<Transaction> findByWalletIdOrderByCreatedAtDesc(Long walletId);

    // Global transaction history for current user
    @Query("SELECT t FROM Transaction t WHERE t.wallet.user.id = :userId ORDER BY t.createdAt DESC")
    List<Transaction> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Find specific transaction and check if user is permitted to view it
    @Query("SELECT t FROM Transaction t JOIN t.wallet w WHERE t.id = :transactionId AND w.user.id = :userId")
    Optional<Transaction> findByUserIdAndTransactionId(Long userId, Long transactionId);

    Boolean existsByTransactionHashAndType(String transactionHash, TransactionType type);
}
