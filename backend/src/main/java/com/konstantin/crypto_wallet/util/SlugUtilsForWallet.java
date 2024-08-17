package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.model.Wallet;
import net.gcardone.junidecode.Junidecode;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SlugUtilsForWallet {
    public String toUniqueSlug(String input, List<Wallet> wallets) {
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

    public Wallet getWalletByUserAndSlug(User user, String slug) {
        return user.getWallets()
                .stream()
                .filter(w -> w.getSlug().equals(slug))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
    }
}
