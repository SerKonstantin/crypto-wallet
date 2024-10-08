package com.konstantin.crypto_wallet.repository;

import com.konstantin.crypto_wallet.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    Optional<List<Wallet>> findByUserId(Long userId);

    Optional<Wallet> findByAddress(String address);
}
