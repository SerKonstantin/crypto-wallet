package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.exception.NotAuthorizedException;
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
            throw new NotAuthorizedException("Not authorized");
        }
        var email = authentication.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotAuthorizedException("Not authorized"));
    }

    public void normalize(User user) {
        user.setNickname(user.getNickname().trim());
        user.setEmail(user.getEmail().toLowerCase().trim());
    }
}
