package com.konstantin.crypto_wallet.dto.wallet;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WalletDTO {
    private Long id;
    private String address;
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
