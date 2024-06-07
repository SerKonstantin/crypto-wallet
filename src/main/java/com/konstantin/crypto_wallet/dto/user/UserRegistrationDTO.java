package com.konstantin.crypto_wallet.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegistrationDTO {

    @NotEmpty
    @Size(min = 3)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "User name must contain only letters, numbers, underscores (_), hyphens (-), and periods (.)")
    private String nickname;

    @NotEmpty
    @Size(min = 8)
    private String password;

    @Email
    @NotEmpty
    private String email;

}
