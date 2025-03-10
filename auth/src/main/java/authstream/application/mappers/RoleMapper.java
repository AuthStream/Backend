package authstream.application.mappers;

import authstream.application.dtos.RoleDto;
import authstream.domain.entities.Role;

public class RoleMapper {

    // DTO -> Entity
    public static Role toEntity(RoleDto dto) {
        if (dto == null) {
            return null;
        }
        Role role = new Role();
        role.setName(dto.name);
        role.setGroupId(dto.groupId);
        role.setPermissionId(dto.permissionId);
        role.setDescription(dto.description);
        return role;
    }

    // Entity -> DTO
    public static RoleDto toDto(Role entity) {
        if (entity == null) {
            return null;
        }
        RoleDto dto = new RoleDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.groupId = entity.getGroupId();
        dto.permissionId = entity.getPermissionId();
        dto.description = entity.getDescription();
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}