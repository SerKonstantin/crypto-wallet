package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.model.User;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Getter
@Component
public class TestUtils {

    @Autowired
    PasswordEncoder passwordEncoder;

    private String passwordInput;
    private User testUser;

    public void generateData() {
        var faker = new Faker();
        passwordInput = faker.internet().password(8, 100);
        var password = passwordEncoder.encode(passwordInput);
        testUser = Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getNickname), () -> faker.name().firstName())
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> password)
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .create();
    }
}
