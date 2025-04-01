package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.dtos.AuthTableConfigDto;
import authstream.application.mappers.AuthTableConfigMapper;
import authstream.application.services.hashing.HashingType;
import authstream.domain.entities.AuthTableConfig;
import authstream.infrastructure.repositories.AuthTableConfigRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class AuthTableConfigService {

    private final AuthTableConfigRepository configRepository;
    private final AuthTableConfigMapper mapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PersistenceContext
    private EntityManager entityManager;

    public AuthTableConfigService(AuthTableConfigRepository configRepository, AuthTableConfigMapper mapper) {
        this.configRepository = configRepository;
        this.mapper = mapper;
    }

    // Lấy tất cả config
    public List<AuthTableConfigDto> getAllConfigs() {
        return configRepository.getAllConfigs().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    // Lấy config theo ID
    public AuthTableConfigDto getConfigById(UUID id) {
        AuthTableConfig config = configRepository.getConfigById(id);
        if (config == null) {
            throw new IllegalArgumentException("Config not found with ID: " + id);
        }
        return mapper.toDto(config);
    }

    // Thêm config mới
    @Transactional
    public AuthTableConfigDto createConfig(AuthTableConfigDto dto) {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        validateConfigDto(dto);

        String hashConfigJson;
        try {
            hashConfigJson = dto.getHashConfig() != null
                    ? objectMapper.writeValueAsString(dto.getHashConfig())
                    : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize hashConfig: " + e.getMessage());
        }
        checkHashingConfig(dto.getHashingType(), hashConfigJson, dto.getSalt());
        int rowsAffected = configRepository.addConfig(id,
                dto.getUserTable(),
                dto.getUsernameAttribute(),
                dto.getPasswordAttribute(),
                dto.getHashingType(),
                dto.getSalt(),
                hashConfigJson,
                now,
                now);
        if (rowsAffected == 0) {
            throw new RuntimeException("Failed to create config");
        }
        AuthTableConfig created = configRepository.getConfigById(id);
        return mapper.toDto(created);
    }

    @Transactional
    public AuthTableConfigDto updateConfig(UUID id, AuthTableConfigDto dto) {
        AuthTableConfig existing = configRepository.getConfigById(id);
        if (existing == null) {
            throw new IllegalArgumentException("Config not found with ID: " + id);
        }
        System.out.println("Before update: " + mapper.toDto(existing));

        // Serialize hashConfig thành chuỗi JSON
        String hashConfigJson;
        try {
            hashConfigJson = dto.getHashConfig() != null
                    ? objectMapper.writeValueAsString(dto.getHashConfig())
                    : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize hashConfig: " + e.getMessage());
        }
        dto.setHashConfig(hashConfigJson);

        validateConfigDto(dto);

        checkHashingConfig(dto.getHashingType(), hashConfigJson, dto.getSalt());
        int rowsAffected = configRepository.updateConfig(
                id,
                dto.getUserTable(),
                dto.getUsernameAttribute(),
                dto.getPasswordAttribute(),
                dto.getHashingType(),
                dto.getSalt(),
                hashConfigJson,
                LocalDateTime.now());
        System.out.println("Rows affected: " + rowsAffected);
        if (rowsAffected == 0) {
            throw new RuntimeException("Failed to update config");
        }
        entityManager.clear();
        AuthTableConfig updated = configRepository.getConfigByIdFresh(id);
        if (updated == null) {
            throw new RuntimeException("Failed to retrieve updated config");
        }
        System.out.println("After update: " + mapper.toDto(updated));
        return mapper.toDto(updated);
    }

    // Xóa config
    @Transactional
    public boolean deleteConfig(UUID id) {
        int rowsAffected = configRepository.deleteConfig(id);
        if (rowsAffected == 0) {
            return false;
        }
        return true;
    }

    private void validateConfigDto(AuthTableConfigDto dto) {
        // Check các trường bắt buộc
        if (dto.getUserTable() == null || dto.getUserTable().trim().isEmpty()) {
            throw new IllegalArgumentException("userTable is required and cannot be empty");
        }
        if (dto.getUsernameAttribute() == null || dto.getUsernameAttribute().trim().isEmpty()) {
            throw new IllegalArgumentException("usernameAttribute is required and cannot be empty");
        }
        if (dto.getPasswordAttribute() == null || dto.getPasswordAttribute().trim().isEmpty()) {
            throw new IllegalArgumentException("passwordAttribute is required and cannot be empty");
        }
        if (dto.getHashingType() == null || dto.getHashingType().trim().isEmpty()) {
            throw new IllegalArgumentException("hashingType is required and cannot be empty");
        }

        // Validate hashingType hợp lệ
        try {
            HashingType.valueOf(dto.getHashingType());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid hashingType: " + dto.getHashingType() +
                    ". Must be one of: " + String.join(", ", getValidHashingTypes()));
        }

        // Validate hashConfig là JSON hợp lệ

        // Salt có thể null, nhưng nếu có thì không được rỗng
        if (dto.getSalt() != null && dto.getSalt().trim().isEmpty()) {
            throw new IllegalArgumentException("salt, if provided, cannot be empty");
        }
    }

    private void checkHashingConfig(String hashingTypeStr, String hashConfig, String externalSalt) {
        HashingType hashingType = HashingType.valueOf(hashingTypeStr);
        JsonNode node;
        try {
            node = objectMapper.readTree(hashConfig);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON in hashConfig: " + e.getMessage());
        }

        // Check salt trong hashConfig nếu có
        if (node.has("salt")) {
            String saltInConfig = node.get("salt").asText();
            if (!externalSalt.equals(saltInConfig)) {
                throw new IllegalArgumentException(
                        "salt in hashConfig (" + saltInConfig + ") must match external salt (" + externalSalt + ")");
            }
        }

        switch (hashingType) {
            case BCRYPT:
                if (!node.has("workFactor")) {
                    throw new IllegalArgumentException("hashConfig for BCRYPT must contain 'workFactor'");
                }
                if (node.get("workFactor").asInt(0) <= 0) {
                    throw new IllegalArgumentException("workFactor for BCRYPT must be a positive integer");
                }
                node.fieldNames().forEachRemaining(field -> {
                    if (!"workFactor".equals(field) && !"salt".equals(field)) {
                        throw new IllegalArgumentException("hashConfig for BCRYPT contains invalid field: " + field
                                + ". Allowed: [workFactor, salt]");
                    }
                });
                break;

            case ARGON2:
                if (!node.has("iterations") || !node.has("memory") || !node.has("parallelism")) {
                    throw new IllegalArgumentException(
                            "hashConfig for ARGON2 must contain 'iterations', 'memory', and 'parallelism'");
                }
                if (node.get("iterations").asInt(0) <= 0 || node.get("memory").asInt(0) <= 0
                        || node.get("parallelism").asInt(0) <= 0) {
                    throw new IllegalArgumentException(
                            "iterations, memory, and parallelism for ARGON2 must be positive integers");
                }
                node.fieldNames().forEachRemaining(field -> {
                    if (!"iterations".equals(field) && !"memory".equals(field) && !"parallelism".equals(field)
                            && !"salt".equals(field)) {
                        throw new IllegalArgumentException("hashConfig for ARGON2 contains invalid field: " + field
                                + ". Allowed: [iterations, memory, parallelism, salt]");
                    }
                });
                break;

            case PBKDF2:
                if (!node.has("iterations") || !node.has("keyLength")) {
                    throw new IllegalArgumentException(
                            "hashConfig for PBKDF2 must contain 'iterations' and 'keyLength'");
                }
                if (node.get("iterations").asInt(0) <= 0 || node.get("keyLength").asInt(0) <= 0) {
                    throw new IllegalArgumentException("iterations and keyLength for PBKDF2 must be positive integers");
                }
                node.fieldNames().forEachRemaining(field -> {
                    if (!"iterations".equals(field) && !"keyLength".equals(field) && !"salt".equals(field)) {
                        throw new IllegalArgumentException("hashConfig for PBKDF2 contains invalid field: " + field
                                + ". Allowed: [iterations, keyLength, salt]");
                    }
                });
                break;

            case SHA256:
                node.fieldNames().forEachRemaining(field -> {
                    if (!"salt".equals(field)) {
                        throw new IllegalArgumentException(
                                "hashConfig for SHA256 contains invalid field: " + field + ". Allowed: [salt]");
                    }
                });
                break;

            case SHA512:
                node.fieldNames().forEachRemaining(field -> {
                    if (!"salt".equals(field)) {
                        throw new IllegalArgumentException(
                                "hashConfig for SHA512 contains invalid field: " + field + ". Allowed: [salt]");
                    }
                });
                break;

            case SCRYPT:
                if (!node.has("n") || !node.has("r") || !node.has("p") || !node.has("keyLength")) {
                    throw new IllegalArgumentException(
                            "hashConfig for SCRYPT must contain 'n', 'r', 'p', and 'keyLength'");
                }
                if (node.get("n").asInt(0) <= 0 || node.get("r").asInt(0) <= 0 || node.get("p").asInt(0) <= 0
                        || node.get("keyLength").asInt(0) <= 0) {
                    throw new IllegalArgumentException("n, r, p, and keyLength for SCRYPT must be positive integers");
                }
                node.fieldNames().forEachRemaining(field -> {
                    if (!"n".equals(field) && !"r".equals(field) && !"p".equals(field) && !"keyLength".equals(field)
                            && !"salt".equals(field)) {
                        throw new IllegalArgumentException("hashConfig for SCRYPT contains invalid field: " + field
                                + ". Allowed: [n, r, p, keyLength, salt]");
                    }
                });
                break;

            default:
                throw new IllegalArgumentException("Unsupported hashing type: " + hashingType);
        }
    }

    private String[] getValidHashingTypes() {
        return java.util.Arrays.stream(HashingType.values())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    public AuthTableConfigDto getSingleConfig() {
        List<AuthTableConfig> configs = configRepository.getAllConfigs();
        if (configs.isEmpty()) {
            throw new IllegalStateException("No auth table config found in database");
        }
        // Lấy config đầu tiên (vì chỉ có 1 hàng)
        AuthTableConfig config = configs.get(0);
        return mapper.toDto(config);
    }
}