package authstream.application.services;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import authstream.application.services.hashing.HashingService;
import authstream.application.services.hashing.HashingType;
import authstream.application.services.kv.TokenEntry;
import authstream.application.services.kv.TokenStoreService;
import authstream.domain.entities.AuthTableConfig;
import authstream.infrastructure.repositories.AuthTableConfigRepository;
import authstream.utils.AuthUtils;;

@Service
public class AuthService {

    private final AuthTableConfigRepository authConfigRepository;
    private final JdbcTemplate jdbcTemplate;

    public AuthService(AuthTableConfigRepository authConfigRepository, JdbcTemplate jdbcTemplate) {
        this.authConfigRepository = authConfigRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> login(String username, String password, String token) throws Exception {
        AuthTableConfig config = authConfigRepository.findFirst();

        if (config == null) {
            throw new IllegalStateException("Auth configuration not found");
        }

        String userTable = config.getUserTable();
        String passwordAttribute = config.getPasswordAttribute();
        HashingType hashingType = config.getHashingType();
        Object hashConfig = AuthUtils.parseHashConfigFromJson(config.getHashConfig(), hashingType);

        System.out.println("userTable: " + userTable);
        System.out.println("passwordAttribute: " + passwordAttribute);
        System.out.println("hashingType: " + hashingType);
        System.out.println("hashConfig: " + hashConfig);

        String hashedInput = HashingService.hash(password, hashingType, hashConfig);
        String storedHash = AuthUtils.fetchUserPassword(username, userTable, passwordAttribute, jdbcTemplate);
        System.out.println("storedHash: " + storedHash);
        if (storedHash == null) {
            throw new IllegalArgumentException("User not found");
        }

        if (hashingType == HashingType.BCRYPT) {
            boolean matches = BCrypt.checkpw(password, storedHash);
            System.out.println("Password matches: " + matches);
            if (!matches) {
                throw new IllegalArgumentException("Invalid username or password");
            }
        } else if (!storedHash.equals(hashedInput)) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        // Check token từ header
        System.out.println("Token received: " + token);
        if (token != null && !token.isEmpty()) {
            TokenEntry existingEntry = TokenStoreService.read(token);
            System.out.println("Existing entry: " + (existingEntry != null ? existingEntry.toString() : "null"));
            if (existingEntry != null && !existingEntry.isExpired()) {
                System.out.println("Token expired: " + existingEntry.isExpired());
                System.out.println("Username in token: " + existingEntry.getMessage().getBody());
                if (existingEntry.getMessage().getBody().equals(username)) {
                    System.out.println("Returning existing token");
                    return AuthUtils.buildTokenResponse(token, existingEntry, "Using existing token");
                } else {
                    throw new IllegalArgumentException("Token does not belong to this user");
                }
            }
        }

        // Sinh token mới
        System.out.println("Generating new token");
        String newToken = UUID.randomUUID().toString();
        TokenEntry tokenEntry = new TokenEntry(
                new TokenEntry.Message(username),
                Instant.now(),
                Instant.now().plusSeconds(3600));
        TokenStoreService.create(newToken, tokenEntry);
        return AuthUtils.buildTokenResponse(newToken, tokenEntry, "New token generated");
    }

    public Map<String, Object> validateToken(String token) {
        TokenEntry entry = TokenStoreService.read(token);
        if (entry == null || entry.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return AuthUtils.buildTokenResponse(token, entry, "Token validated");
    }

    public static void main(String[] args) {
        String password = "pass123";
        String salt = "$2a$12$Gbe4AzAQpfwu5bYRWhpiD.";
        String hash = BCrypt.hashpw(password, salt);
        System.out.println("Generated hash: " + hash);
        boolean matches = BCrypt.checkpw(password, hash);
        System.out.println("Matches with generated hash: " + matches);
        boolean matchesWithStored = BCrypt.checkpw(password,
                "$2a$12$Gbe4AzAQpfwu5bYRWhpiD.GraMcNnFJLnB914N2yEZsZCE6C.1wa6");
        System.out.println("Matches with stored hash: " + matchesWithStored);
    }

}