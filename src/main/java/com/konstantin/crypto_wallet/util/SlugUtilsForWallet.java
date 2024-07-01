package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.model.Wallet;
import net.gcardone.junidecode.Junidecode;

import java.util.List;

public class SlugUtilsForWallet {
    public static String toUniqueSlug(String input, List<Wallet> wallets) {
        StringBuilder slug = new StringBuilder(Junidecode.unidecode(input)
                .trim()
                .toLowerCase()
                .replaceAll("[^\\w-]", ""));

        int counter = 1;
        String finalVariableSlug = slug.toString();
        while (wallets.stream().anyMatch(wallet -> wallet.getSlug().equals(finalVariableSlug))) {
            slug.append("-").append(counter);
            counter++;
        }

        return slug.toString();
    }
}
