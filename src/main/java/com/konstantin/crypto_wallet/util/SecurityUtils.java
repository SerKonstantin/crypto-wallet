package com.konstantin.crypto_wallet.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("securityUtils")
public class SecurityUtils {

    @Autowired
    private UserUtils userUtils;

    public boolean hasUserId(Long userId) {
        var currentUser = userUtils.getCurrentUser();
        return currentUser != null && currentUser.getId().equals(userId);
    }
}
