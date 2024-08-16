package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import com.konstantin.crypto_wallet.repository.TransactionRepository;
import com.konstantin.crypto_wallet.repository.UserRepository;
import com.konstantin.crypto_wallet.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.stereotype.Component;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@Component
public class PredefinedTestDataInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public PredefinedTestData initializeData() {
        var user = new User();
        user.setNickname("TestUser");
        user.setEmail("testuser@testmail.com");

        var wallet = new Wallet();
        wallet.setAddress(EnvironmentUtils.getEnvVariable("TEST_WALLET_ADDRESS"));
        wallet.setName("Test Wallet 1");
        wallet.setSlug("testwallet1");
        wallet.setUser(user);

        var receiverWallet = new Wallet();
        receiverWallet.setAddress(EnvironmentUtils.getEnvVariable("RECEIVER_TEST_WALLET_ADDRESS"));
        receiverWallet.setName("Test Wallet 2");
        receiverWallet.setSlug("testwallet2");
        receiverWallet.setUser(user);

        user.getWallets().add(wallet);
        user.getWallets().add(receiverWallet);

        userRepository.save(user);
        walletRepository.save(wallet);
        walletRepository.save(receiverWallet);

        var token = jwt().jwt(builder -> builder.subject(user.getUsername()));

        var privateKey = EnvironmentUtils.getEnvVariable("TEST_WALLET_PRIVATE_KEY");

        return new PredefinedTestData(user, wallet, receiverWallet, token, privateKey);
    }

    public void cleanRelatedRepositories() {
        transactionRepository.deleteAll();
        walletRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Getter
    @AllArgsConstructor
    public static class PredefinedTestData {
        private final User user;
        private final Wallet wallet;
        private final Wallet receiverWallet;
        private final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;
        private final String privateKey;
    }
}
