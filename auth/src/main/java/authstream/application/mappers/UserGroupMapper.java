package authstream.application.mappers;

import authstream.application.dtos.UserGroupDto;
import authstream.domain.entities.UserGroup;

public class UserGroupMapper {

    // DTO -> Entity
    public static UserGroup toEntity(UserGroupDto dto) {
        if (dto == null) {
            return null;
        }
        UserGroup userGroup = new UserGroup();
        userGroup.setUserId(dto.userId);
        userGroup.setGroupId(dto.groupId);
        return userGroup;
    }

    // Entity -> DTO
    public static UserGroupDto toDto(UserGroup entity) {
        if (entity == null) {
            return null;
        }
        UserGroupDto dto = new UserGroupDto();
        dto.userId = entity.getUserId();
        dto.groupId = entity.getGroupId();
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}