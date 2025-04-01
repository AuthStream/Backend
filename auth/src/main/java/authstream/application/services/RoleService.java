package authstream.application.services;

import authstream.application.dtos.RoleDto;
import authstream.domain.entities.Group;
import authstream.domain.entities.Role;
import authstream.application.mappers.RoleMapper;
import authstream.infrastructure.repositories.RoleRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final ObjectMapper objectMapper;

    public RoleService(RoleRepository roleRepository, ObjectMapper objectMapper) {
        this.roleRepository = roleRepository;
        this.objectMapper = objectMapper;
    }

    public RoleDto createRole(RoleDto dto) {
        parsePermissionId(dto.permissionId);

        Role role = RoleMapper.toEntity(dto);
        role.setId(UUID.randomUUID());
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());

        int status = roleRepository.addRole(
                role.getId(),
                role.getName(),
                role.getGroupId(),
                role.getPermissionId(),
                role.getDescription(),
                role.getCreatedAt(),
                role.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Role creation failed");
        }

        return RoleMapper.toDto(role);
    }

    public RoleDto updateRole(UUID id, RoleDto dto) {
        parsePermissionId(dto.permissionId);

        int status = roleRepository.updateRole(
                id,
                dto.name,
                dto.groupId,
                dto.permissionId,
                dto.description,
                LocalDateTime.now());
        if (status == 0) {
            throw new RuntimeException("Role update failed");
        }

        Role updated = roleRepository.getRoleById(id);
        return RoleMapper.toDto(updated);
    }

    public void deleteRole(UUID id) {
        int status = roleRepository.deleteRole(id);
        if (status == 0) {
            throw new RuntimeException("Role deletion failed");
        }
    }

    public RoleDto getRoleById(UUID id) {
        Role role = roleRepository.getRoleById(id);
        if (role == null) {
            throw new RuntimeException("Role not found");
        }
        return RoleMapper.toDto(role);
    }

    public List<RoleDto> getAllRoles() {
        List<Role> roles = roleRepository.getAllRoles();
        return roles.stream()
                .map(RoleMapper::toDto)
                .toList();
    }

    public Pair<Role, Object> findById(UUID roleId) {

        try {
            Role role = roleRepository.getRoleById(roleId);

            if (role == null) {
                return Pair.of(null, "role not found");

            }
            return Pair.of(role, null);

        } catch (Exception e) {
            return Pair.of(null, "Something wrong with sever: " + e.getMessage());
        }

    }

    private void parsePermissionId(String permissionIdJson) {
        try {
            List<String> permissionIds = objectMapper.readValue(
                    permissionIdJson,
                    new TypeReference<List<String>>() {
                    });
            if (permissionIds == null || permissionIds.isEmpty()) {
                throw new IllegalArgumentException("Permission IDs cannot be empty");
            }
            for (String permId : permissionIds) {
                if (permId == null || permId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Each permission ID must be a non-empty string");
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid permission_id JSON: " + e.getMessage());
        }
    }
}