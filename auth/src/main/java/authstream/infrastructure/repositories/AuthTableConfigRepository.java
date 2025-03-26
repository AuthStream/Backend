package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.AuthTableConfig;
import jakarta.transaction.Transactional;

@Repository
public interface AuthTableConfigRepository extends JpaRepository<AuthTableConfig, UUID> {

        String getAllConfigsQuery = "SELECT * FROM auth_table_config";
        String getConfigByIdQuery = "SELECT * FROM auth_table_config WHERE id = :id";
        String updateConfigByIdQuery = "UPDATE auth_table_config SET user_table = :userTable, " +
"username_attribute = :usernameAttribute," +     
                "password_attribute = :passwordAttribute, hashing_type = :hashingType, " +
                        "salt = :salt, hash_config = cast(:hashConfig as jsonb), updated_at = :updatedAt " +
                        "WHERE id = :id";
        String deleteConfigByIdQuery = "DELETE FROM auth_table_config WHERE id = :id";
        String addConfigQuery = "INSERT INTO auth_table_config (id, user_table,username_attribute, password_attribute, "
                        +
                        "hashing_type, salt, hash_config, created_at, updated_at) " +
                        "VALUES (:id, :userTable,:usernameAttribute ,:passwordAttribute, :hashingType, :salt, cast(:hashConfig as jsonb), "
                        +
                        ":createdAt, :updatedAt)";

        @Query(value = getAllConfigsQuery, nativeQuery = true)
        List<AuthTableConfig> getAllConfigs();

        @Query(value = getConfigByIdQuery, nativeQuery = true)
        AuthTableConfig getConfigById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = updateConfigByIdQuery, nativeQuery = true)
        int updateConfig(
                        @Param("id") UUID id,
                        @Param("userTable") String userTable,
                        @Param("usernameAttribute") String usernameAttribute,
                        @Param("passwordAttribute") String passwordAttribute,
                        @Param("hashingType") String hashingType,
                        @Param("salt") String salt,
                        @Param("hashConfig") String hashConfig,
                        @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = deleteConfigByIdQuery, nativeQuery = true)
        int deleteConfig(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addConfigQuery, nativeQuery = true)
        int addConfig(
                        @Param("id") UUID id,
                        @Param("userTable") String userTable,
                        @Param("usernameAttribute") String usernameAttribute,
                        @Param("passwordAttribute") String passwordAttribute,
                        @Param("hashingType") String hashingType,
                        @Param("salt") String salt,
                        @Param("hashConfig") String hashConfig,
                        @Param("createdAt") LocalDateTime createdAt,
                        @Param("updatedAt") LocalDateTime updatedAt);

        default AuthTableConfig findFirst() {
                return getAllConfigs().stream().findFirst().orElse(null);
        }

        @Query(value = "SELECT * FROM auth_table_config WHERE id = :id", nativeQuery = true)
        AuthTableConfig getConfigByIdFresh(@Param("id") UUID id);
}