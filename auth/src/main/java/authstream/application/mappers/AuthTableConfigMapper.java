package authstream.application.mappers;

import org.springframework.stereotype.Component;

import authstream.application.dtos.AuthTableConfigDto;
import authstream.domain.entities.AuthTableConfig;

@Component
public class AuthTableConfigMapper {

    public AuthTableConfigDto toDto(AuthTableConfig entity) {
        if (entity == null) {
            return null;
        }
        return new AuthTableConfigDto(
                entity.getId(),
                entity.getUserTable(),
                entity.getPasswordAttribute(),
                entity.getHashingType().name(), // Chuyển enum thành String
                entity.getSalt(),
                entity.getHashConfig() // Giữ nguyên dạng String JSON
        );
    }

    public AuthTableConfig toEntity(AuthTableConfigDto dto) {
        if (dto == null) {
            return null;
        }
        AuthTableConfig entity = new AuthTableConfig();
        entity.setId(dto.getId());
        entity.setUserTable(dto.getUserTable());
        entity.setPasswordAttribute(dto.getPasswordAttribute());
        entity.setHashingType(dto.getHashingType() != null
                ? Enum.valueOf(authstream.application.services.hashing.HashingType.class, dto.getHashingType())
                : null);
        entity.setSalt(dto.getSalt());
        entity.setHashConfig((String) dto.getHashConfig()); // Ép kiểu về String vì DB lưu JSON
        return entity;
    }
}