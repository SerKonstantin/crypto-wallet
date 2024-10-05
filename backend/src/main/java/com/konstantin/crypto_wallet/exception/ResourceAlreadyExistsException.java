package com.konstantin.crypto_wallet.exception;

import lombok.Getter;

@Getter
public class ResourceAlreadyExistsException extends RuntimeException {
    private final String resourceName;

    public ResourceAlreadyExistsException(String resourceName) {
        super("The " + resourceName + " you provided is already in use. Please choose a different one.");
        this.resourceName = resourceName;
    }
}
