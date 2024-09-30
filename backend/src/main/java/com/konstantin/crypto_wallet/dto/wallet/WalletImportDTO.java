package com.konstantin.crypto_wallet.dto.wallet;

import com.konstantin.crypto_wallet.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletImportDTO {

    @NotBlank
    private String privateKey;

    @NotBlank
    @Size(min = Constants.MIN_WALLET_NAME_LENGTH, max = Constants.MAX_UNIFIED_LENGTH,
            message = "Wallet name must be at least " + Constants.MIN_WALLET_NAME_LENGTH + " characters long.")
    private String name;

}
