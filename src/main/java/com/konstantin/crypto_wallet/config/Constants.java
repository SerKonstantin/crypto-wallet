package com.konstantin.crypto_wallet.config;

public final class Constants {
    private Constants() {
        //to prevent instantiation
    }

    public static final long TOKEN_EXPIRATION_TIME = 3600000L; // 1 hour in milliseconds
    public static final int MIN_NICKNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;

}
