package authstream.application.mappers;

import authstream.application.dtos.PermissionDto;
import authstream.domain.entities.Permission;

public class PermissionMapper {

    // DTO -> Entity
    public static Permission toEntity(PermissionDto dto) {
        if (dto == null) {
            return null;
        }

        Permission permission = new Permission();
        permission.setName(dto.name);
        permission.setApiRoutes(dto.apiRoutes);
        permission.setDescription(dto.description);

        return permission;
    }

    public static PermissionDto toDto(Permission entity) {
        if (entity == null) {
            return null;
        }
        PermissionDto dto = new PermissionDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.apiRoutes = entity.getApiRoutes();
        dto.description = entity.getDescription();
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}