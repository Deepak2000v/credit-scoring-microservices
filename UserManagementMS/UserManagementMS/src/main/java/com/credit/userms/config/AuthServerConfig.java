package com.credit.userms.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AuthServerConfig {

    @Value("${jwt.secret}")
    private String jwtSecret;

    // Token valid for 1 day
    private static final long EXPIRATION_TIME = 86400000L;

    /**
     * Generate a signed JWT token for a logged-in user.
     */
    public String generateToken(String username, String role) {
        return JWT.create()
                .withSubject(username)
                .withClaim("role", role)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(jwtSecret));
    }

    /**
     * Validate token and return the username (subject) if valid.
     * Returns null if the token is invalid or expired.
     */
    public String validateToken(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(jwtSecret))
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
