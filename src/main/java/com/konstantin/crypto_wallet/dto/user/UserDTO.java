package com.konstantin.crypto_wallet.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String createdAt;
    private String updatedAt;
}
