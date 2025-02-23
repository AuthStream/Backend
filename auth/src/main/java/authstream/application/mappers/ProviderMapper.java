package authstream.application.mappers;

import authstream.application.dtos.ProviderDto;
import authstream.domain.entities.Provider;

public class ProviderMapper {

    public static Provider toEntity(ProviderDto dto) {
        if (dto == null) {
            return null;
        }
        Provider provider = new Provider();
        provider.setApplicationId(dto.applicationId); // Có thể null
        provider.setMethodId(dto.methodId); // Có thể null
        provider.setType(dto.type);
        provider.setName(dto.name);
        return provider;
    }

    public static ProviderDto toDto(Provider entity) {
        if (entity == null) {
            return null;
        }
        ProviderDto dto = new ProviderDto();
        dto.id = entity.getId();
        dto.applicationId = entity.getApplicationId();
        dto.methodId = entity.getMethodId();
        dto.type = entity.getType();
        dto.name = entity.getName();
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}