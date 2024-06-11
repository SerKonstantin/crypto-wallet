package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.exception.ResourceNotFoundException;
import com.konstantin.crypto_wallet.model.User;
import com.konstantin.crypto_wallet.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class UserUtils {

    @Autowired
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public static void normalize(User user) {
        user.setNickname(user.getNickname().trim());
        user.setEmail(user.getEmail().toLowerCase().trim());
    }
}
