package authstream.presentation.controllers;

import authstream.application.dtos.UserGroupDto;
import authstream.application.services.UserGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-groups")
public class UserGroupController {

    private final UserGroupService userGroupService;

    public UserGroupController(UserGroupService userGroupService) {
        this.userGroupService = userGroupService;
    }

    @PostMapping
    public ResponseEntity<UserGroupDto> createUserGroup(@RequestBody UserGroupDto dto) {
        UserGroupDto created = userGroupService.createUserGroup(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<UserGroupDto> updateUserGroup(
            @PathVariable UUID userId,
            @PathVariable UUID groupId) {
        UserGroupDto updated = userGroupService.updateUserGroup(userId, groupId);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<Void> deleteUserGroup(
            @PathVariable UUID userId,
            @PathVariable UUID groupId) {
        userGroupService.deleteUserGroup(userId, groupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/group/{groupId}")
    public ResponseEntity<UserGroupDto> getUserGroupByIds(
            @PathVariable UUID userId,
            @PathVariable UUID groupId) {
        UserGroupDto userGroup = userGroupService.getUserGroupByIds(userId, groupId);
        return ResponseEntity.ok(userGroup);
    }

    @GetMapping
    public ResponseEntity<List<UserGroupDto>> getAllUserGroups() {
        List<UserGroupDto> userGroups = userGroupService.getAllUserGroups();
        return ResponseEntity.ok(userGroups);
    }
}