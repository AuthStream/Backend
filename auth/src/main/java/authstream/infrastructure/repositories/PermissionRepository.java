package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import authstream.domain.entities.Permission;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

        String getAllPermissionsQuery = "SELECT * FROM permissions";
        String getPermissionByIdQuery = "SELECT * FROM permissions WHERE id = :id";
        String addPermissionQuery = "INSERT INTO permissions (id, name, api_routes, description, created_at, updated_at) "
                        +
                        "VALUES (:id, :name, CAST(:apiRoutes AS jsonb), :description, :createdAt, :updatedAt)";
        String updatePermissionQuery = "UPDATE permissions SET name = :newName, api_routes = CAST(:newApiRoutes AS jsonb), "
                        +
                        "description = :newDescription, updated_at = :newUpdatedAt WHERE id = :id";
        String deletePermissionQuery = "DELETE FROM permissions WHERE id = :id";

        @Query(value = getAllPermissionsQuery, nativeQuery = true)
        List<Permission> getAllPermissions();

        @Query(value = getPermissionByIdQuery, nativeQuery = true)
        Permission getPermissionById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addPermissionQuery, nativeQuery = true)
        int addPermission(
                        @Param("id") UUID id,
                        @Param("name") String name,
                        @Param("apiRoutes") String apiRoutes,
                        @Param("description") String description,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = updatePermissionQuery, nativeQuery = true)
        int updatePermission(
                        @Param("id") UUID id,
                        @Param("newName") String newName,
                        @Param("newApiRoutes") String newApiRoutes,
                        @Param("newDescription") String newDescription,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

        @Modifying
        @Transactional
        @Query(value = deletePermissionQuery, nativeQuery = true)
        int deletePermission(@Param("id") UUID id);
}