package com.konstantin.crypto_wallet.dto.wallet;

import com.konstantin.crypto_wallet.config.Constants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WalletUpdateDTO {

    @NotBlank
    @Size(min = Constants.MIN_WALLET_NAME_LENGTH)
    private String name;
}
