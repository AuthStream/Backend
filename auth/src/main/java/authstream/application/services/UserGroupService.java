package authstream.application.services;

import authstream.application.dtos.UserGroupDto;
import authstream.domain.entities.User;
import authstream.domain.entities.UserGroup;
import authstream.application.mappers.UserGroupMapper;
import authstream.infrastructure.repositories.UserGroupRepository;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserGroupService {

    private final UserGroupRepository userGroupRepository;

    public UserGroupService(UserGroupRepository userGroupRepository) {
        this.userGroupRepository = userGroupRepository;
    }

    public UserGroupDto createUserGroup(UserGroupDto dto) {
        UserGroup userGroup = UserGroupMapper.toEntity(dto);
        userGroup.setCreatedAt(LocalDateTime.now());
        userGroup.setUpdatedAt(LocalDateTime.now());

        int status = userGroupRepository.addUserGroup(
                userGroup.getUserId(),
                userGroup.getGroupId(),
                userGroup.getCreatedAt(),
                userGroup.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("UserGroup creation failed");
        }

        return UserGroupMapper.toDto(userGroup);
    }

    public UserGroupDto updateUserGroup(UUID userId, UUID groupId) {
        int status = userGroupRepository.updateUserGroup(
                userId,
                groupId,
                LocalDateTime.now());
        if (status == 0) {
            throw new RuntimeException("UserGroup update failed");
        }

        UserGroup updated = userGroupRepository.getUserGroupByIds(userId, groupId);
        return UserGroupMapper.toDto(updated);
    }

    public void deleteUserGroup(UUID userId, UUID groupId) {
        int status = userGroupRepository.deleteUserGroup(userId, groupId);
        if (status == 0) {
            throw new RuntimeException("UserGroup deletion failed");
        }
    }

    public UserGroupDto getUserGroupByIds(UUID userId, UUID groupId) {
        UserGroup userGroup = userGroupRepository.getUserGroupByIds(userId, groupId);
        if (userGroup == null) {
            throw new RuntimeException("UserGroup not found");
        }
        return UserGroupMapper.toDto(userGroup);
    }

    public List<UserGroupDto> getAllUserGroups() {
        List<UserGroup> userGroups = userGroupRepository.getAllUserGroups();
        return userGroups.stream()
                .map(UserGroupMapper::toDto)
                .toList();
    }

    public Pair<List<UserGroup>, Object> findByUserId(UUID userId) {

        try {
            List<UserGroup> userGroups = userGroupRepository.findByUserId(userId);

            if (userGroups == null) {
                return Pair.of(null, "userGroup not found");

            }
            return Pair.of(userGroups, null);

        } catch (Exception e) {
            return Pair.of(null, "Something wrong with sever: " + e.getMessage());
        }
    }
}