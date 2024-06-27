package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import io.github.cdimascio.dotenv.Dotenv;

public class EnvironmentUtils {
    public static String getInfuraApiKey() {
        String infuraApiKey;

        if (System.getenv("INFURA_API_KEY") != null) {
            infuraApiKey = System.getenv("INFURA_API_KEY");
        } else {
            var dotenv = Dotenv.configure().ignoreIfMissing().load();
            infuraApiKey = dotenv.get("INFURA_API_KEY");
        }

        if (infuraApiKey == null) {
            throw new ResourceNotFoundException("Api key for infura connection is not provided");
        }
        return infuraApiKey;
    }
}
