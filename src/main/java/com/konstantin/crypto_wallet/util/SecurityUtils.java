package com.konstantin.crypto_wallet.util;

import jakarta.servlet.http.HttpServletRequest;
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

    public static String parseTokenFromHeader(HttpServletRequest request) {
        final String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        }
        return null;
    }
}
