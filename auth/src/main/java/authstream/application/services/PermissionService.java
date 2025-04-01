package authstream.application.services;

import authstream.application.dtos.ApiRoute;
import authstream.application.dtos.PermissionDto;
import authstream.domain.entities.Permission;
import authstream.domain.entities.Role;
import authstream.application.mappers.PermissionMapper;
import authstream.infrastructure.repositories.PermissionRepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final ObjectMapper objectMapper;

    public PermissionService(PermissionRepository permissionRepository, ObjectMapper objectMapper) {
        this.permissionRepository = permissionRepository;
        this.objectMapper = objectMapper;
    }

    public PermissionDto createPermission(PermissionDto dto) {
        parseApiRoutes(dto.apiRoutes);

        Permission permission = PermissionMapper.toEntity(dto);
        permission.setId(UUID.randomUUID());
        permission.setCreatedAt(LocalDateTime.now());
        permission.setUpdatedAt(LocalDateTime.now());

        int status = permissionRepository.addPermission(
                permission.getId(),
                permission.getName(),
                permission.getApiRoutes(),
                permission.getDescription(),
                permission.getCreatedAt(),
                permission.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Permission creation failed");
        }

        return PermissionMapper.toDto(permission);
    }

    public PermissionDto updatePermission(UUID id, PermissionDto dto) {
        parseApiRoutes(dto.apiRoutes);

        int status = permissionRepository.updatePermission(
                id,
                dto.name,
                dto.apiRoutes,
                dto.description,
                LocalDateTime.now());
        if (status == 0) {
            throw new RuntimeException("Permission update failed");
        }

        Permission updated = permissionRepository.getPermissionById(id);
        return PermissionMapper.toDto(updated);
    }

    public void deletePermission(UUID id) {
        int status = permissionRepository.deletePermission(id);
        if (status == 0) {
            throw new RuntimeException("Permission deletion failed");
        }
    }

    public PermissionDto getPermissionById(UUID id) {
        Permission permission = permissionRepository.getPermissionById(id);
        if (permission == null) {
            throw new RuntimeException("Permission not found");
        }
        return PermissionMapper.toDto(permission);
    }

    public List<PermissionDto> getAllPermissions() {
        List<Permission> permissions = permissionRepository.getAllPermissions();
        return permissions.stream()
                .map(PermissionMapper::toDto)
                .toList();
    }

    public Pair<Permission, Object> findById(UUID permissionId) {
        try {
            Permission pemission = permissionRepository.getPermissionById(permissionId);

            if (pemission == null) {
                return Pair.of(null, "permission not found");

            }
            return Pair.of(pemission, null);

        } catch (Exception e) {
            return Pair.of(null, "Something wrong with sever: " + e.getMessage());
        }
    }

    private void parseApiRoutes(String apiRoutesJson) {
        try {
            List<ApiRoute> routes = objectMapper.readValue(
                    apiRoutesJson,
                    new TypeReference<List<ApiRoute>>() {
                    });
            for (ApiRoute route : routes) {
                if (route.getPath() == null || !route.getPath().startsWith("/")) {
                    throw new IllegalArgumentException("Path must start with '/'");
                }
                if (route.getMethod() == null || route.getMethod().isEmpty()) {
                    throw new IllegalArgumentException("At least one method is required");
                }
                String method = route.getMethod();
                if (!List.of("GET", "POST", "PUT", "DELETE", "PATCH").contains(method.toUpperCase())) {
                    throw new IllegalArgumentException("Invalid HTTP method: " + method);
                }
            }
        } catch (JsonProcessingException | IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid api_routes JSON: " + e.getMessage());
        }
    }
}