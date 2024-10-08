package com.konstantin.crypto_wallet.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TransactionRequestDTO {

    @NotBlank
    private String fromAddress;

    @NotBlank
    private String toAddress;

    @Positive
    private BigInteger amount;

    @Positive
    private BigInteger gasPrice;

    @Positive
    private BigInteger gasLimit;

    @Positive
    private BigInteger fee;

    @Positive
    private BigInteger total;

    @NotBlank
    private String privateKey;

}
