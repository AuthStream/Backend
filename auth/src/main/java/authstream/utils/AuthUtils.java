package authstream.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.services.hashing.HashingType;
import authstream.application.services.hashing.config.Argon2Config;
import authstream.application.services.hashing.config.BcryptConfig;
import authstream.application.services.hashing.config.Pbkdf2Config;
import authstream.application.services.hashing.config.ScryptConfig;
import authstream.application.services.hashing.config.Sha256Config;
import authstream.application.services.hashing.config.Sha512Config;
import authstream.application.services.kv.TokenEntry;

public class AuthUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Parse cấu hình hash từ JSON
    public static Object parseHashConfigFromJson(String jsonConfig, HashingType hashingType) throws Exception {
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

    // Lấy password từ DB

    public static String fetchUserPassword(String username, String userTable, String passwordAttribute,
            JdbcTemplate jdbcTemplate) {
        String sql = "SELECT " + passwordAttribute + " FROM " + userTable + " WHERE username = ?";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, username);
        } catch (Exception e) {
            return null;
        }
    }

    // Tạo response cho token
    public static Map<String, Object> buildTokenResponse(String token, TokenEntry entry, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", entry.getMessage().getBody());
        response.put("createdAt", entry.getCreatedAt().toString());
        response.put("expiredAt", entry.getExpired().toString());
        response.put("ttl", entry.getRemainingTTL());
        response.put("message", message);
        return response;
    }
}