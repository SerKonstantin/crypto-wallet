package com.konstantin.crypto_wallet.config;

public final class Constants {
    private Constants() {
        //to prevent instantiation
    }

    public static final long AUTH_TOKEN_EXPIRATION_TIME = 3600000L; // 1 hour in milliseconds
    public static final int MIN_NICKNAME_LENGTH = 3;
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MIN_WALLET_NAME_LENGTH = 3;
    public static final int MIN_EMAIL_LENGTH = 5;
    public static final int MAX_UNIFIED_LENGTH = 254;
    public static final long TRANSACTIONS_STATUS_CHECK_INTERVAL = 15000L; // 15 seconds

}
