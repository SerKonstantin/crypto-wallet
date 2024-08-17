package com.konstantin.crypto_wallet.controller;

import com.konstantin.crypto_wallet.dto.auth.AuthRequest;
import com.konstantin.crypto_wallet.service.TokenBlacklistService;
import com.konstantin.crypto_wallet.util.JWTUtils;
import com.konstantin.crypto_wallet.util.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    @PostMapping("/login")
    public String create(@RequestBody AuthRequest authRequest) {
        var authentication = new UsernamePasswordAuthenticationToken(
                authRequest.getUsername().toLowerCase().trim(), authRequest.getPassword().trim());

        authenticationManager.authenticate(authentication);

        var token = jwtUtils.generateAuthToken(authRequest.getUsername());
        return token;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = SecurityUtils.parseTokenFromHeader(request);
        if (token != null) {
            tokenBlacklistService.addToken(token);
            return ResponseEntity.ok("Successfully logged out");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token not provided");
        }
    }
}
