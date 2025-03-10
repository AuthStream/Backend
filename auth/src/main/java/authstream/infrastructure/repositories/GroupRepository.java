package authstream.infrastructure.repositories;

import authstream.domain.entities.Group;
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
public interface GroupRepository extends JpaRepository<Group, UUID> {

        String getAllGroupsQuery = "SELECT * FROM groups";
        String getGroupByIdQuery = "SELECT * FROM groups WHERE id = :id";
        String addGroupQuery = "INSERT INTO groups (id, name, role_id, descriptions, created_at, updated_at) " +
                        "VALUES (:id, :name, CAST(:roleId AS jsonb), :descriptions, :createdAt, :updatedAt)";
        String updateGroupQuery = "UPDATE groups SET name = :newName, role_id = CAST(:newRoleId AS jsonb), " +
                        "descriptions = :newDescriptions, updated_at = :newUpdatedAt WHERE id = :id";
        String deleteGroupQuery = "DELETE FROM groups WHERE id = :id";

        @Query(value = getAllGroupsQuery, nativeQuery = true)
        List<Group> getAllGroups();

        @Query(value = getGroupByIdQuery, nativeQuery = true)
        Group getGroupById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addGroupQuery, nativeQuery = true)
        int addGroup(
                        @Param("id") UUID id,
                        @Param("name") String name,
                        @Param("roleId") String roleId,
                        @Param("descriptions") String descriptions,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = updateGroupQuery, nativeQuery = true)
        int updateGroup(
                        @Param("id") UUID id,
                        @Param("newName") String newName,
                        @Param("newRoleId") String newRoleId,
                        @Param("newDescriptions") String newDescriptions,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

        @Modifying
        @Transactional
        @Query(value = deleteGroupQuery, nativeQuery = true)
        int deleteGroup(@Param("id") UUID id);
}