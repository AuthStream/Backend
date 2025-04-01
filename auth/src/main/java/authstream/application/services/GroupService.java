package authstream.application.services;

import authstream.application.dtos.GroupDto;
import authstream.domain.entities.Group;
import authstream.domain.entities.UserGroup;
import authstream.application.mappers.GroupMapper;
import authstream.infrastructure.repositories.GroupRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final ObjectMapper objectMapper;

    public GroupService(GroupRepository groupRepository, ObjectMapper objectMapper) {
        this.groupRepository = groupRepository;
        this.objectMapper = objectMapper;
    }

    public GroupDto createGroup(GroupDto dto) {
        parseRoleId(dto.roleId);

        Group group = GroupMapper.toEntity(dto);
        group.setId(UUID.randomUUID());
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());

        int status = groupRepository.addGroup(
                group.getId(),
                group.getName(),
                group.getRoleId(),
                group.getDescriptions(),
                group.getCreatedAt(),
                group.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Group creation failed");
        }

        return GroupMapper.toDto(group);
    }

    public GroupDto updateGroup(UUID id, GroupDto dto) {
        parseRoleId(dto.roleId);

        int status = groupRepository.updateGroup(
                id,
                dto.name,
                dto.roleId,
                dto.descriptions,
                LocalDateTime.now());
        if (status == 0) {
            throw new RuntimeException("Group update failed");
        }

        Group updated = groupRepository.getGroupById(id);
        return GroupMapper.toDto(updated);
    }

    public void deleteGroup(UUID id) {
        int status = groupRepository.deleteGroup(id);
        if (status == 0) {
            throw new RuntimeException("Group deletion failed");
        }
    }

    public GroupDto getGroupById(UUID id) {
        Group group = groupRepository.getGroupById(id);
        if (group == null) {
            throw new RuntimeException("Group not found");
        }
        return GroupMapper.toDto(group);
    }

    public List<GroupDto> getAllGroups() {
        List<Group> groups = groupRepository.getAllGroups();
        return groups.stream()
                .map(GroupMapper::toDto)
                .toList();
    }

    private void parseRoleId(String roleIdJson) {
        try {
            List<String> roleIds = objectMapper.readValue(
                    roleIdJson,
                    new TypeReference<List<String>>() {
                    });
            if (roleIds == null || roleIds.isEmpty()) {
                throw new IllegalArgumentException("Role IDs cannot be empty");
            }
            for (String roleId : roleIds) {
                if (roleId == null || roleId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Each role ID must be a non-empty string");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid role_id JSON: " + e.getMessage());
        }
    }

    public Pair<Group, Object> findById(UUID groupId) {
        try {
            Group group = groupRepository.getGroupById(groupId);

            if (group == null) {
                return Pair.of(null, "group not found");

            }
            return Pair.of(group, null);

        } catch (Exception e) {
            return Pair.of(null, "Something wrong with sever: " + e.getMessage());
        }
    }
}