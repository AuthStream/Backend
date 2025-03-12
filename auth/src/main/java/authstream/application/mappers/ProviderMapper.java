package authstream.application.mappers;

import authstream.application.dtos.ProviderDto;
import authstream.domain.entities.Provider;
import java.time.LocalDateTime;
public class ProviderMapper {

    public static Provider toEntity(ProviderDto dto) {
        if (dto == null) {
            return null;
        }
        return Provider.builder()
                .id(dto.id)
                .applicationId(dto.applicationId)
                .methodId(dto.methodId)
                .type(dto.type)
                .name(dto.name)
                .createdAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now())
                .updatedAt(dto.updatedAt != null ? dto.updatedAt : LocalDateTime.now())
                .build();
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