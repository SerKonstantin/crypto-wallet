package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.config.Constants;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@Component
public class RandomTestDataGenerator {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    private String passwordInput;

    public RandomTestData generateData() {
        var user = generateUser();
        var wallet = generateWallet(user);
        user.getWallets().add(wallet);
        userRepository.save(user);
        walletRepository.save(wallet);
        var token = generateToken(user);
        return new RandomTestData(user, passwordInput, token, wallet);
    }

    public void cleanAllRepositories() {
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    private User generateUser() {
        var faker = new Faker();
        passwordInput = faker.internet().password(Constants.MIN_PASSWORD_LENGTH, 100);
        var password = passwordEncoder.encode(passwordInput);

        return Instancio.of(User.class)
                .ignore(Select.field(User::getId))
                .supply(Select.field(User::getNickname), this::generateNicknameWithMinLength)
                .supply(Select.field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(Select.field(User::getPassword), () -> password)
                .ignore(Select.field(User::getCreatedAt))
                .ignore(Select.field(User::getUpdatedAt))
                .supply(Select.field(User::getWallets), () -> new ArrayList())
                .create();
    }

    private String generateNicknameWithMinLength() {
        var faker = new Faker();
        String nickname;
        do {
            nickname = faker.name().firstName();
        } while (nickname.length() <= Constants.MIN_NICKNAME_LENGTH);
        return nickname;
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor generateToken(User user) {
        return jwt().jwt(builder -> builder.subject(user.getEmail()));
    }

    private Wallet generateWallet(User user) {
        var faker = new Faker();
        var walletName = faker.lorem().word() + " wallet";
        var slug = walletName.replaceAll(" ", "").toLowerCase();
        return Instancio.of(Wallet.class)
                .ignore(Select.field(Wallet::getId))
                .supply(Select.field(Wallet::getAddress), () -> "0x" + faker.regexify("[a-f0-9]{40}"))
                .supply(Select.field(Wallet::getName), () -> walletName)
                .supply(Select.field(Wallet::getSlug), () -> slug)
                .ignore(Select.field(Wallet::getCreatedAt))
                .ignore(Select.field(Wallet::getUpdatedAt))
                .supply(Select.field(Wallet::getUser), () -> user)
                .supply(Select.field(Wallet::getTransactions), () -> new ArrayList())
                .create();
    }

    @Getter
    @AllArgsConstructor
    public class RandomTestData {
        private final User user;
        private final String passwordInput;
        private final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
        private final Wallet wallet;
    }
}
