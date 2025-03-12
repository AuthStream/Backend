package authstream.application.mappers;

import authstream.application.dtos.ApplicationDto;
import authstream.domain.entities.Application;
import authstream.domain.entities.Provider;
import authstream.domain.entities.Token;

import java.time.LocalDateTime;

public class ApplicationMapper {

    public static Application toEntity(ApplicationDto dto) {
        if (dto == null) {
            return null;
        }
        Application application = new Application();
        application.setName(dto.name);
        application.setAdminId(dto.adminId);
        if (dto.providerId != null) {
            Provider provider = new Provider();
            provider.setId(dto.providerId);
            application.setProvider(provider);
        }
        if (dto.tokenId != null) {
            Token token = new Token();
            token.setId(dto.tokenId);
            application.setToken(token);
        }
        application.setCreatedAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now());
        application.setUpdatedAt(dto.updatedAt != null ? dto.updatedAt : LocalDateTime.now());
        return application;
    }

    public static ApplicationDto toDto(Application entity) {
        if (entity == null) {
            return null;
        }
        ApplicationDto dto = new ApplicationDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.adminId = entity.getAdminId();
        dto.providerId = entity.getProvider() != null ? entity.getProvider().getId() : null;
        dto.tokenId = entity.getToken() != null ? entity.getToken().getId() : null;
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}