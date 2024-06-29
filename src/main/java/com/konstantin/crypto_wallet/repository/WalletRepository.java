package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<List<Wallet>> findByUserId(Long userId);
}
