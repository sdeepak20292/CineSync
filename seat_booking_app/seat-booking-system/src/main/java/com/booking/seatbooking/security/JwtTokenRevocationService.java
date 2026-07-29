package com.booking.seatbooking.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;

/** Stores a token fingerprint until its natural expiry, making logout immediate. */
@Service
public class JwtTokenRevocationService {

    private static final String REVOKED_TOKEN_PREFIX = "jwt:revoked:";

    private final StringRedisTemplate redisTemplate;
    private final JwtService jwtService;

    public JwtTokenRevocationService(StringRedisTemplate redisTemplate, JwtService jwtService) {
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
    }

    public void revoke(String token) {
        long remainingValidityMs = jwtService.getRemainingValidityMs(token);
        if (remainingValidityMs <= 0) return;

        redisTemplate.opsForValue().set(
                REVOKED_TOKEN_PREFIX + fingerprint(token),
                "1",
                Duration.ofMillis(remainingValidityMs)
        );
    }

    public boolean isRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REVOKED_TOKEN_PREFIX + fingerprint(token)));
    }

    private String fingerprint(String token) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
