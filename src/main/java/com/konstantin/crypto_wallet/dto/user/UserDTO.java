package com.konstantin.crypto_wallet.dto.user;

import com.konstantin.crypto_wallet.dto.wallet.WalletDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String nickname;
    private String email;
    private String role;
    private List<WalletDTO> wallets;
    private String createdAt;
    private String updatedAt;
}
