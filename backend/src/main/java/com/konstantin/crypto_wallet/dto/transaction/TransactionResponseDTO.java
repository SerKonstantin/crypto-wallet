package com.konstantin.crypto_wallet.dto.transaction;

import com.konstantin.crypto_wallet.model.transaction.TransactionStatus;
import com.konstantin.crypto_wallet.model.transaction.TransactionType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionResponseDTO {
    private Long id;
    private String fromAddress;
    private String toAddress;
    private TransactionType type;
    private BigInteger amount;
    private BigInteger fee;
    private BigInteger total;
    private String transactionHash;
    private LocalDateTime createdAt;
    private TransactionStatus status;
}
