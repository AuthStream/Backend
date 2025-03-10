package authstream.infrastructure.repositories;

import authstream.domain.entities.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

        String getAllRolesQuery = "SELECT * FROM roles";
        String getRoleByIdQuery = "SELECT * FROM roles WHERE id = :id";
        String addRoleQuery = "INSERT INTO roles (id, name, group_id, permission_id, description, created_at, updated_at) "
                        +
                        "VALUES (:id, :name, :groupId, CAST(:permissionId AS jsonb), :description, :createdAt, :updatedAt)";
        String updateRoleQuery = "UPDATE roles SET name = :newName, group_id = :newGroupId, " +
                        "permission_id = CAST(:newPermissionId AS jsonb), description = :newDescription, " +
                        "updated_at = :newUpdatedAt WHERE id = :id";
        String deleteRoleQuery = "DELETE FROM roles WHERE id = :id";

        @Query(value = getAllRolesQuery, nativeQuery = true)
        List<Role> getAllRoles();

        @Query(value = getRoleByIdQuery, nativeQuery = true)
        Role getRoleById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addRoleQuery, nativeQuery = true)
        int addRole(
                        @Param("id") UUID id,
                        @Param("name") String name,
                        @Param("groupId") UUID groupId,
                        @Param("permissionId") String permissionId,
                        @Param("description") String description,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = updateRoleQuery, nativeQuery = true)
        int updateRole(
                        @Param("id") UUID id,
                        @Param("newName") String newName,
                        @Param("newGroupId") UUID newGroupId,
                        @Param("newPermissionId") String newPermissionId,
                        @Param("newDescription") String newDescription,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

        @Modifying
        @Transactional
        @Query(value = deleteRoleQuery, nativeQuery = true)
        int deleteRole(@Param("id") UUID id);
}