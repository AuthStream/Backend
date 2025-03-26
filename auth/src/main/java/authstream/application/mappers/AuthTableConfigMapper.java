package authstream.application.mappers;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.dtos.AuthTableConfigDto;
import authstream.domain.entities.AuthTableConfig;

@Component
public class AuthTableConfigMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public AuthTableConfigDto toDto(AuthTableConfig entity) {
        if (entity == null) {
            return null;
        }
        Object hashConfig;
        try {
            // Deserialize chuỗi JSON thành Object
            hashConfig = entity.getHashConfig() != null
                    ? objectMapper.readValue(entity.getHashConfig(), Object.class)
                    : null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize hashConfig: " + e.getMessage());
        }
        return new AuthTableConfigDto(
                entity.getId(),
                entity.getUserTable(),
                entity.getUsernameAttribute(),
                entity.getPasswordAttribute(),
                entity.getHashingType().name(), // Chuyển enum thành String
                entity.getSalt(),
                hashConfig
        );
    }

    public AuthTableConfig toEntity(AuthTableConfigDto dto) {
        if (dto == null) {
            return null;
        }
        AuthTableConfig entity = new AuthTableConfig();
        entity.setId(dto.getId());
        entity.setUserTable(dto.getUserTable());
        entity.setUsernameAttribute(dto.getUsernameAttribute());
        entity.setPasswordAttribute(dto.getPasswordAttribute());
        entity.setHashingType(dto.getHashingType() != null
                ? Enum.valueOf(authstream.application.services.hashing.HashingType.class, dto.getHashingType())
                : null);
        entity.setSalt(dto.getSalt());
        // Serialize Object thành chuỗi JSON
        try {
            entity.setHashConfig(dto.getHashConfig() != null
                    ? objectMapper.writeValueAsString(dto.getHashConfig())
                    : null);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize hashConfig: " + e.getMessage());
        }
        return entity;
    }
}