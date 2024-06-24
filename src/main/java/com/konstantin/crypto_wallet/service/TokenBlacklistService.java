package com.konstantin.crypto_wallet.service;

import com.konstantin.crypto_wallet.config.Constants;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBlacklistService {

    private final Map<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public void addToken(String token) {
        long expirationTime = System.currentTimeMillis() + Constants.TOKEN_EXPIRATION_TIME;
        tokenBlacklist.put(token, expirationTime);
    }

    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    @Scheduled(fixedRate = Constants.TOKEN_EXPIRATION_TIME)
    public void cleanUpExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

}
