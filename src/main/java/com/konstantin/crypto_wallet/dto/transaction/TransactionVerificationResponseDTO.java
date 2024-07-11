package com.konstantin.crypto_wallet.dto.transaction;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TransactionVerificationResponseDTO {
    private String fromAddress;
    private String toAddress;
    private BigInteger amount;
    private BigInteger fee;
    private BigInteger total;
    private boolean sufficientFunds;
    private String verificationToken;
}
