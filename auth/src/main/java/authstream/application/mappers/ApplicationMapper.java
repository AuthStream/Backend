package authstream.application.mappers;

import authstream.application.dtos.ApplicationDto;
import authstream.domain.entities.Application;
import authstream.domain.entities.Provider;

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
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}