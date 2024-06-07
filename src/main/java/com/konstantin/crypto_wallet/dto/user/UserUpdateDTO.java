package com.konstantin.crypto_wallet.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @Size(min = 3)
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$",
            message = "User name must contain only letters, numbers, underscores (_), hyphens (-), and periods (.)")
    private JsonNullable<String> nickname;

    @Size(min = 8)
    private JsonNullable<String> password;

    @Email
    private JsonNullable<String> email;

}
