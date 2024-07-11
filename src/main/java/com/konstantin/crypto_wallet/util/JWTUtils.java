package com.konstantin.crypto_wallet.util;

import com.konstantin.crypto_wallet.config.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

@Component
public class JWTUtils {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    public String generateAuthToken(String username) {
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(Constants.AUTH_TOKEN_EXPIRATION_TIME, ChronoUnit.MILLIS))
                .subject(username)
                .build();
        return this.jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public String generateTransactionVerificationToken(Map<String, Object> claimsMap) {
        Instant now = Instant.now();
        JwtClaimsSet.Builder claimsBuilder = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(Constants.TRANSACTION_VERIFICATION_TOKEN_EXPIRATION_TIME, ChronoUnit.MILLIS));

        claimsMap.forEach(claimsBuilder::claim);

        return this.jwtEncoder.encode(JwtEncoderParameters.from(claimsBuilder.build())).getTokenValue();
    }

    public Map<String, Object> validateTransactionVerificationToken(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        return new HashMap<>(jwt.getClaims());
    }

}
