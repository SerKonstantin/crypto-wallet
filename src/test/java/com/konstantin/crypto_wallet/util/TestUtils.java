package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.config.Constants;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Getter
@Component
public class TestUtils {

    @Autowired
    PasswordEncoder passwordEncoder;

    private String passwordInput;
    private User testUser;
    private Wallet testWallet;

    public void generateData() {
        testUser = generateUser();
        testWallet = generateWallet(testUser);
        testUser.getWallets().add(testWallet);
    }

    private User generateUser() {
        var faker = new Faker();
        passwordInput = faker.internet().password(Constants.MIN_PASSWORD_LENGTH, 100);
        var password = passwordEncoder.encode(passwordInput);

        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getNickname), this::generateNickname)
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> password)
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getWallets), () -> new ArrayList())
                .create();
    }

    private Wallet generateWallet(User user) {
        var faker = new Faker();
        var walletName = faker.lorem().word() + " wallet";
        var slug = walletName.replaceAll(" ", "").toLowerCase();
        return Instancio.of(Wallet.class)
                .ignore(Select.field(Wallet::getId))
                .supply(Select.field(Wallet::getAddress), () -> faker.regexify("[a-f0-9]{64}"))
                .supply(Select.field(Wallet::getName), () -> walletName)
                .supply(Select.field(Wallet::getSlug), () -> slug)
                .ignore(Select.field(Wallet::getCreatedAt))
                .ignore(Select.field(Wallet::getUpdatedAt))
                .supply(Select.field(Wallet::getUser), () -> user)
                .create();
    }

    private String generateNickname() {
        var faker = new Faker();
        String nickname;
        do {
            nickname = faker.name().firstName();
        } while (nickname.length() <= Constants.MIN_NICKNAME_LENGTH);
        return nickname;
    }
}
