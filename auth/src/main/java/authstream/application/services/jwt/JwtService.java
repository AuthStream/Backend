package authstream.application.services.jwt;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import authstream.application.services.kv.TokenEntry;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class JwtService {
    public static TokenResponse createToken(Object claims, String secretKey, long ttlSeconds) {
        if (claims == null || secretKey == null) {
            throw new IllegalArgumentException("Claims, secret key, and algorithm must not be null");
        }
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL must be positive");
        }

        Instant now = Instant.now();
        Date issuedAt = Date.from(now);

        Instant expiration = now.plusSeconds(ttlSeconds);
        Date expiresAt = Date.from(expiration);

        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String token = Jwts.builder()
                .setClaims(claims instanceof Map ? (Map<String, ?>) claims : null)
                .setIssuedAt(issuedAt)
                .setExpiration(expiresAt)
                .signWith(key)
                .compact();
        TokenEntry.Message message = new TokenEntry.Message(claims);
        TokenEntry tokenEntry = new TokenEntry(message, now, expiration);

        return new TokenResponse(token, tokenEntry);
    }
    public static void main(String[] args) {
        try {
            // Create sample claims
            Map<String, Object> claims = new HashMap<>();
            claims.put("userId", "12345");
            claims.put("username", "john_doe");
            claims.put("role", "USER");

            // Define secret key and TTL (time to live)
            String secretKey = "mySuperSecretKey1234567890123456"; // Should be at least 32 chars for HS256
            long ttlSeconds = 3600; // 1 hour in seconds

            // Create token using JwtService
            TokenResponse tokenResponse = JwtService.createToken(claims, secretKey, ttlSeconds);

            System.out.println("Generated JWT Token: " + tokenResponse.getToken());
            System.out.println("Token Entry Details:");
            System.out.println("Claims: " + tokenResponse.getTokenEntry().getMessage());
            System.out.println("Issued At: " + tokenResponse.getTokenEntry());
            System.out.println("Expires At: " + tokenResponse.getTokenEntry());

        } catch (IllegalArgumentException e) {
            System.err.println("Error creating token: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}