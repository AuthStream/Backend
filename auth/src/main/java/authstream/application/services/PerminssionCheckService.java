package authstream.application.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.domain.entities.Group;
import authstream.domain.entities.Permission;
import authstream.domain.entities.Role;
import authstream.domain.entities.User;
import authstream.domain.entities.UserGroup;

@Service
public class PerminssionCheckService {

    private static final Logger logger = LoggerFactory.getLogger(PerminssionCheckService.class);

    private final UserService userService;
    private final UserGroupService userGroupService;
    private final GroupService groupService;
    private final RoleService roleService;
    private final PermissionService permissionService;
    private final ObjectMapper objectMapper;

    public PerminssionCheckService(UserService userService, UserGroupService userGroupService,
            GroupService groupService, RoleService roleService, PermissionService permissionService) {
        this.userService = userService;
        this.userGroupService = userGroupService;
        this.groupService = groupService;
        this.roleService = roleService;
        this.permissionService = permissionService;
        this.objectMapper = new ObjectMapper();
    }

    public Pair<Boolean, Object> checkPermission(String username, String route, String method) {
        try {
            // 1. Lấy userId từ username
            Pair<User, Object> userPair = userService.findByUsername(username);
            if (userPair.getRight() != null) {
                // logger.warn("User not found for username: {}", username);
                return Pair.of(false, userPair.getRight());
            }
            UUID userId = userPair.getLeft().getId();
            logger.info("Found userId: {} for username: {}", userId, username);

            // 2. Lấy groupId từ userId
            Pair<List<UserGroup>, Object> userGroupsPair = userGroupService.findByUserId(userId);
            if (userGroupsPair.getRight() != null) {
                logger.warn("No groups found for userId: {}", userId);
                return Pair.of(false, userGroupsPair.getRight());
            }

            UUID groupId = userGroupsPair.getLeft().get(0).getGroupId();

            logger.info("Found groupId: {} for userId: {}", groupId, userId);

            // 3. Lấy roleId từ groupId
            Pair<Group, Object> groupPair = groupService.findById(groupId);
            if (groupPair.getRight() != null) {
                logger.warn("Group not found for groupId: {}", groupId);
                return Pair.of(false, groupPair.getRight());
            }
            String roleIdJson = groupPair.getLeft().getRoleId();
            List<UUID> roleIds = objectMapper.readValue(roleIdJson, new TypeReference<List<UUID>>() {
            });
            if (roleIds.isEmpty()) {
                logger.warn("No roles found for groupId: {}", groupId);
                return Pair.of(false, "No roles assigned to group");
            }
            logger.info("Found roleIds: {} for groupId: {}", roleIds, groupId);

            // 4. Lấy permissionId từ roleId
            List<String> allPermissionIds = new ArrayList<>();
            for (UUID roleId : roleIds) {
                Pair<Role, Object> rolePair = roleService.findById(roleId);
                if (rolePair.getRight() != null) {
                    continue;
                }
                if (rolePair.getLeft() != null) {
                    String permissionIdJson = rolePair.getLeft().getPermissionId();
                    List<String> permissionIds = objectMapper.readValue(permissionIdJson,
                            new TypeReference<List<String>>() {
                            });
                    allPermissionIds.addAll(permissionIds);
                }
            }
            if (allPermissionIds.isEmpty()) {
                logger.warn("No permissions found for roles: {}", roleIds);
                return Pair.of(false, "No permissions assigned to roles");
            }
            logger.info("Found permissionIds: {} for roles: {}", allPermissionIds, roleIds);

            // 5. Lấy apiRoutes từ permissionId và check route/method
            for (String permissionId : allPermissionIds) {
                Pair<Permission, Object> permissionPair = permissionService.findById(UUID.fromString(permissionId));
                logger.info("oke bo may da vao den doan apiRoutes:{}", permissionPair);
                if (permissionPair.getRight() != null) {
                    logger.info("cokhinao loi do cai nay:{}", permissionPair);
                    continue;
                }

                Permission permission = permissionPair.getLeft();
                if (permission != null) {
                    String apiRoutesJson = permission.getApiRoutes();
                    List<Map<String, String>> apiRoutes = objectMapper.readValue(apiRoutesJson,
                            new TypeReference<List<Map<String, String>>>() {
                            });
                    for (Map<String, String> apiRoute : apiRoutes) {
                        String permittedRoute = apiRoute.get("path");
                        String permittedMethod = apiRoute.get("method");
                        if (route.startsWith(permittedRoute) && method.equalsIgnoreCase(permittedMethod)) {
                            logger.info("Permission granted for route: {} (method: {}) with permission: {}",
                                    route, method, permission.getName());
                            return Pair.of(true, null);
                        }
                    }
                }
            }

            logger.warn("No permission found for route: {} (method: {}) for user: {}", route, method, username);
            return Pair.of(false, "Permission denied");

        } catch (Exception e) {
            logger.error("Error checking permission for user: {}, route: {}, method: {}", username, route, method, e);
            return Pair.of(false, "Error checking permission: " + e.getMessage());
        }
    }

}