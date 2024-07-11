package com.konstantin.crypto_wallet.model.transaction;

import com.konstantin.crypto_wallet.model.Wallet;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions", indexes = @Index(name = "idx_transaction_hash", columnList = "transactionHash"))
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @NotBlank
    private String fromAddress;

    @NotBlank
    private String toAddress;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionType type;

    @Positive
    private BigInteger amount;

    @PositiveOrZero
    private BigInteger fee;

    @NotNull
    @Column(unique = true)
    private String transactionHash;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @NotNull
    private TransactionStatus status;
}
