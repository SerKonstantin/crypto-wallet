package com.konstantin.crypto_wallet.exception;

public class PendingTransactionException extends RuntimeException {
    public PendingTransactionException(String message) {
        super(message);
    }
}
