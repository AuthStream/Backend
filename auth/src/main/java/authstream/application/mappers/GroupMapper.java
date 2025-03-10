package authstream.application.mappers;

import authstream.application.dtos.GroupDto;
import authstream.domain.entities.Group;

public class GroupMapper {

    // DTO -> Entity
    public static Group toEntity(GroupDto dto) {
        if (dto == null) {
            return null;
        }
        Group group = new Group();
        group.setName(dto.name);
        group.setRoleId(dto.roleId);
        group.setDescriptions(dto.descriptions);
        return group;
    }

    // Entity -> DTO
    public static GroupDto toDto(Group entity) {
        if (entity == null) {
            return null;
        }
        GroupDto dto = new GroupDto();
        dto.id = entity.getId();
        dto.name = entity.getName();
        dto.roleId = entity.getRoleId();
        dto.descriptions = entity.getDescriptions();
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}