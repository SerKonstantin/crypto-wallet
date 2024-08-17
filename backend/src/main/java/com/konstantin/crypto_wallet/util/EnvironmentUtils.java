package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentUtils {
    public static String getEnvVariable(String variableName) {
        String value;

        if (System.getenv(variableName) != null) {
            value = System.getenv(variableName);
        } else {
            var dotenv = Dotenv.configure().ignoreIfMissing().load();
            value = dotenv.get(variableName);
        }

        if (value == null) {
            throw new ResourceNotFoundException("Environment variable " + variableName + " is not provided");
        }

        return value;
    }
}
