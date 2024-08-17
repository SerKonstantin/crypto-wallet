package com.konstantin.crypto_wallet.dto.user;

import com.konstantin.crypto_wallet.config.Constants;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @NotEmpty
    @Size(min = Constants.MIN_NICKNAME_LENGTH)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "User name must contain only letters, numbers, underscores (_), hyphens (-), and periods (.)")
    private JsonNullable<String> nickname;

    @NotEmpty
    @Size(min = Constants.MIN_PASSWORD_LENGTH)
    private JsonNullable<String> password;

    @NotEmpty
    @Email
    private JsonNullable<String> email;

}
