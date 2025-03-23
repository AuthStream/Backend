package authstream.application.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.services.hashing.HashingService;
import authstream.application.services.hashing.HashingType;
import authstream.application.services.hashing.config.Argon2Config;
import authstream.application.services.hashing.config.BcryptConfig;
import authstream.application.services.hashing.config.Pbkdf2Config;
import authstream.application.services.hashing.config.ScryptConfig;
import authstream.application.services.hashing.config.Sha256Config;
import authstream.application.services.hashing.config.Sha512Config;
import authstream.application.services.kv.TokenEntry;
import authstream.application.services.kv.TokenStoreService;
import authstream.domain.entities.AuthTableConfig;
import authstream.infrastructure.repositories.AuthTableConfigRepository;

@Service
public class AuthService {

    private final AuthTableConfigRepository authConfigRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public AuthService(AuthTableConfigRepository authConfigRepository, JdbcTemplate jdbcTemplate) {
        this.authConfigRepository = authConfigRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    // @Transactional(readOnly = true)
    // public Map<String, Object> login(String username, String password, String token) throws Exception {
    //     // Lấy cấu hình từ auth_table_config
    //     AuthTableConfig config = authConfigRepository.findFirst();
    //     if (config == null) {
    //         throw new IllegalStateException("Auth configuration not found");
    //     }

    //     // Lấy thông tin từ config
    //     String userTable = config.getUserTable();
    //     String passwordAttribute = config.getPasswordAttribute();
    //     HashingType hashingType = config.getHashingType();
    //     Object hashConfig = parseHashConfigFromJson(config.getHashConfig(), hashingType);

    //     System.out.print(userTable);
    //     System.out.print(passwordAttribute);
    //     System.out.print(hashingType);
    //     System.out.print(hashConfig);
    //     // Hash password từ request
    //     String hashedInput = HashingService.hash(password, hashingType, hashConfig);

    //     // Lấy password đã hash từ DB
    //     String storedHash = fetchUserPassword(username, userTable, passwordAttribute);
    //     if (storedHash == null) {
    //         throw new IllegalArgumentException("User not found");
    //     }

    //     // Check password
    //     if (hashingType == HashingType.BCRYPT) {
    //         if (!BCrypt.checkpw(password, storedHash)) {
    //             throw new IllegalArgumentException("Invalid username or password");
    //         }
    //     } else if (!storedHash.equals(hashedInput)) {
    //         throw new IllegalArgumentException("Invalid username or password");
    //     }

    //     // Check token từ header
    //     if (token != null && !token.isEmpty()) {
    //         TokenEntry existingEntry = TokenStoreService.read(token);
    //         if (existingEntry != null && !existingEntry.isExpired()) {
    //             // Token cũ còn hợp lệ, trả về thông tin
    //             if (existingEntry.getMessage().getBody().equals(username)) {
    //                 return buildTokenResponse(token, existingEntry, "Using existing token");
    //             } else {
    //                 throw new IllegalArgumentException("Token does not belong to this user");
    //             }
    //         }
    //     }

    //     // Token không hợp lệ hoặc không có, sinh token mới
    //     String newToken = UUID.randomUUID().toString();
    //     TokenEntry tokenEntry = new TokenEntry(
    //             new TokenEntry.Message(username),
    //             Instant.now(),
    //             Instant.now().plusSeconds(3600) // TTL: 1 giờ
    //     );
    //     TokenStoreService.create(newToken, tokenEntry);
    //     return buildTokenResponse(newToken, tokenEntry, "New token generated");
    // }


    @Transactional(readOnly = true)
public Map<String, Object> login(String username, String password, String token) throws Exception {
    AuthTableConfig config = authConfigRepository.findFirst();
    
    if (config == null) {
        throw new IllegalStateException("Auth configuration not found");
    }

    String userTable = config.getUserTable();
    String passwordAttribute = config.getPasswordAttribute();
    HashingType hashingType = config.getHashingType();
    Object hashConfig = parseHashConfigFromJson(config.getHashConfig(), hashingType);

    System.out.println("userTable: " + userTable);
    System.out.println("passwordAttribute: " + passwordAttribute);
    System.out.println("hashingType: " + hashingType);
    System.out.println("hashConfig: " + hashConfig);

    String hashedInput = HashingService.hash(password, hashingType, hashConfig);
    String storedHash = fetchUserPassword(username, userTable, passwordAttribute);
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
                return buildTokenResponse(token, existingEntry, "Using existing token");
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
            Instant.now().plusSeconds(3600)
    );
    TokenStoreService.create(newToken, tokenEntry);
    return buildTokenResponse(newToken, tokenEntry, "New token generated");
}
    public Map<String, Object> validateToken(String token) {
        TokenEntry entry = TokenStoreService.read(token);
        if (entry == null || entry.isExpired()) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return buildTokenResponse(token, entry, "Token validated");
    }

    private String fetchUserPassword(String username, String userTable, String passwordAttribute) {
        String sql = "SELECT " + passwordAttribute + " FROM " + userTable + " WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (Exception e) {
            return null;
        }
    }

    private Object parseHashConfigFromJson(String jsonConfig, HashingType hashingType) throws Exception {
        JsonNode node = objectMapper.readTree(jsonConfig);

        switch (hashingType) {
            case BCRYPT:
                int workFactor = node.has("workFactor") ? node.get("workFactor").asInt() : 12;
                String bcryptSalt = node.has("salt") ? node.get("salt").asText() : null;
                return BcryptConfig.builder()
                        .workFactor(workFactor)
                        .salt(bcryptSalt)
                        .build();

            case ARGON2:
                String argon2Salt = node.has("salt") ? node.get("salt").asText() : null;
                int iterations = node.has("iterations") ? node.get("iterations").asInt() : 1;
                int memory = node.has("memory") ? node.get("memory").asInt() : 65536;
                int parallelism = node.has("parallelism") ? node.get("parallelism").asInt() : 1;
                return Argon2Config.builder()
                        .salt(argon2Salt)
                        .iterations(iterations)
                        .memory(memory)
                        .parallelism(parallelism)
                        .build();

            case PBKDF2:
                String pbkdf2Salt = node.has("salt") ? node.get("salt").asText() : null;
                int pbkdf2Iterations = node.has("iterations") ? node.get("iterations").asInt() : 1000;
                int keyLength = node.has("keyLength") ? node.get("keyLength").asInt() : 256;
                return Pbkdf2Config.builder()
                        .salt(pbkdf2Salt)
                        .iterations(pbkdf2Iterations)
                        .keyLength(keyLength)
                        .build();

            case SHA256:
                String sha256Salt = node.has("salt") ? node.get("salt").asText() : null;
                return Sha256Config.builder()
                        .salt(sha256Salt)
                        .build();

            case SHA512:
                String sha512Salt = node.has("salt") ? node.get("salt").asText() : null;
                return Sha512Config.builder()
                        .salt(sha512Salt)
                        .build();

            case SCRYPT:
                String scryptSalt = node.has("salt") ? node.get("salt").asText() : null;
                int n = node.has("n") ? node.get("n").asInt() : 16384;
                int r = node.has("r") ? node.get("r").asInt() : 8;
                int p = node.has("p") ? node.get("p").asInt() : 1;
                int scryptKeyLength = node.has("keyLength") ? node.get("keyLength").asInt() : 32;
                return ScryptConfig.builder()
                        .salt(scryptSalt)
                        .n(n)
                        .r(r)
                        .p(p)
                        .keyLength(scryptKeyLength)
                        .build();

            default:
                throw new IllegalArgumentException("Unsupported hashing type: " + hashingType);
        }
    }

    private Map<String, Object> buildTokenResponse(String token, TokenEntry entry, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", entry.getMessage().getBody());
        response.put("createdAt", entry.getCreatedAt().toString());
        response.put("expiredAt", entry.getExpired().toString());
        response.put("ttl", entry.getRemainingTTL());
        response.put("message", message);
        return response;
    }
    // public static void main(String[] args) {
        
    //     String hashed = "$2a$10$Gbe4AzAQpfwu5bYRWhpiD.XvgSpBd.8hBNRBSCkqoWtZpRx/lEp5y"; // Thay bằng hash thực tế trong DB
    //     boolean matches = BCrypt.checkpw("pass123", hashed);
    //     System.out.println("Matches: " + matches);
    //     String newHash = BCrypt.hashpw("pass123", BCrypt.gensalt(10));
    //     System.out.println("New hash for pass123: " + newHash);
    // }

    public static void main(String[] args) {
        // Test password
        String storedHash = "$2a$10$Gbe4AzAQpfwu5bYRWhpiD.XvgSpBd.8hBNRBSCkqoWtZpRx/lEp5y";
        boolean matches = BCrypt.checkpw("pass123", storedHash);
        System.out.println("Password matches: " + matches);
        String newHash = BCrypt.hashpw("pass123", BCrypt.gensalt(10));
        System.out.println("New hash for pass123: " + newHash);
    
        // Test token
        String username = "test";
        String existingToken = "4fe8090e-0081-4279-815b-d56e3e7a3abe";
    
        // Tạo token cũ giả lập
        TokenEntry oldEntry = new TokenEntry(
                new TokenEntry.Message(username),
                Instant.now().minusSeconds(600),
                Instant.now().plusSeconds(3600)
        );
        TokenStoreService.create(existingToken, oldEntry);
    
        // Giả lập login với token cũ
        TokenEntry readEntry = TokenStoreService.read(existingToken);
        if (readEntry != null && !readEntry.isExpired() && readEntry.getMessage().getBody().equals(username)) {
            System.out.println("Using existing token: " + existingToken);
            System.out.println("Token details: " + readEntry.toString());
            return; // Thoát nếu token hợp lệ
        }
    
        // Sinh token mới nếu token cũ không hợp lệ
        String newToken = UUID.randomUUID().toString();
        TokenEntry newEntry = new TokenEntry(
                new TokenEntry.Message(username),
                Instant.now(),
                Instant.now().plusSeconds(3600)
        );
        TokenStoreService.create(newToken, newEntry);
        System.out.println("Generated new token: " + newToken);
        System.out.println("New token details: " + newEntry.toString());
    }
}