package com.konstantin.crypto_wallet.dto.user;

import com.konstantin.crypto_wallet.config.Constants;
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
    @Size(min = Constants.MIN_NICKNAME_LENGTH, max = Constants.MAX_UNIFIED_LENGTH,
            message = "Nickname must be at least " + Constants.MIN_NICKNAME_LENGTH + " characters long.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "Nickname can contain only letters, numbers, underscores (_), hyphens (-), and periods (.)")
    private String nickname;

    @NotEmpty
    @Size(min = Constants.MIN_PASSWORD_LENGTH, max = Constants.MAX_UNIFIED_LENGTH,
            message = "Password must be at least " + Constants.MIN_PASSWORD_LENGTH + " characters long.")
    private String password;

    @Email
    @NotEmpty
    @Size(min = Constants.MIN_EMAIL_LENGTH, max = Constants.MAX_UNIFIED_LENGTH,
            message = "Email must be at least " + Constants.MIN_EMAIL_LENGTH + " characters long.")
    private String email;

}
