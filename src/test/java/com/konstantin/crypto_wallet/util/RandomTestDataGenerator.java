package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.config.Constants;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.datafaker.Faker;
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

        var user = new User();
        user.setNickname(generateNickname());
        user.setEmail(faker.internet().emailAddress());
        user.setPassword(password);
        user.setWallets(new ArrayList<>());

        return user;
    }

    private String generateNickname() {
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

        var wallet = new Wallet();
        wallet.setAddress("0x" + faker.regexify("[a-f0-9]{40}"));
        wallet.setName(walletName);
        wallet.setSlug(slug);
        wallet.setUser(user);
        wallet.setTransactions(new ArrayList<>());

        return wallet;
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
