package authstream.infrastructure.repositories;

import authstream.domain.entities.UserGroup;
import authstream.domain.entities.UserGroupId;
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
public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {

        String getAllUserGroupsQuery = "SELECT * FROM user_group";
        String getUserGroupByIdsQuery = "SELECT * FROM user_group WHERE user_id = :userId AND group_id = :groupId";
        String addUserGroupQuery = "INSERT INTO user_group (user_id, group_id, created_at, updated_at) " +
                        "VALUES (:userId, :groupId, :createdAt, :updatedAt)";
        String updateUserGroupQuery = "UPDATE user_group SET updated_at = :newUpdatedAt " +
                        "WHERE user_id = :userId AND group_id = :groupId";
        String deleteUserGroupQuery = "DELETE FROM user_group WHERE user_id = :userId AND group_id = :groupId";

        @Query(value = getAllUserGroupsQuery, nativeQuery = true)
        List<UserGroup> getAllUserGroups();

        @Query(value = getUserGroupByIdsQuery, nativeQuery = true)
        UserGroup getUserGroupByIds(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

        @Modifying
        @Transactional
        @Query(value = addUserGroupQuery, nativeQuery = true)
        int addUserGroup(
                        @Param("userId") UUID userId,
                        @Param("groupId") UUID groupId,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = updateUserGroupQuery, nativeQuery = true)
        int updateUserGroup(
                        @Param("userId") UUID userId,
                        @Param("groupId") UUID groupId,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

        @Modifying
        @Transactional
        @Query(value = deleteUserGroupQuery, nativeQuery = true)
        int deleteUserGroup(@Param("userId") UUID userId, @Param("groupId") UUID groupId);

        @Query(value = "SELECT * FROM user_group WHERE user_id = :userId", nativeQuery = true)
        List<UserGroup> findByUserId(@Param("userId") UUID userId);
}