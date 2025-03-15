package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.Admin;
import jakarta.transaction.Transactional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {

        String addAdminQuery =  "INSERT INTO admins (id, username, password, uri, database_username, database_password, " +
            "database_type, ssl_mode, connection_string, table_include_list, schema_include_list, " +
            "collection_include_list, created_at, updated_at) " +
            "VALUES (:id, :username, :password, :uri, :databaseUsername, :databasePassword, :databaseType, " +
            ":sslMode, :connectionString, :tableIncludeList, :schemaIncludeList, :collectionIncludeList, " +
            ":createdAt, :updatedAt)";
    // Tạo mới admin
    @Modifying
    @Transactional
    @Query(value = addAdminQuery, nativeQuery = true)
    int createAdmin(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("password") String password,
            @Param("uri") String uri,
            @Param("databaseUsername") String databaseUsername,
            @Param("databasePassword") String databasePassword,
            @Param("databaseType") String databaseType,
            @Param("sslMode") String sslMode,
            @Param("connectionString") String connectionString,
            @Param("tableIncludeList") String tableIncludeList,
            @Param("schemaIncludeList") String schemaIncludeList,
            @Param("collectionIncludeList") String collectionIncludeList,
            @Param("createdAt") LocalDateTime createdAt,
            @Param("updatedAt") LocalDateTime updatedAt);

    // Cập nhật admin
    @Modifying
    @Transactional
    @Query(value = "UPDATE admins SET username = :username, password = :password, uri = :uri, " +
            "database_username = :databaseUsername, database_password = :databasePassword, " +
            "database_type = :databaseType, ssl_mode = :sslMode, connection_string = :connectionString, " +
            "table_include_list = :tableIncludeList, schema_include_list = :schemaIncludeList, " +
            "collection_include_list = :collectionIncludeList, updated_at = :updatedAt " +
            "WHERE id = :id", nativeQuery = true)
    int updateAdmin(
            @Param("id") UUID id,
            @Param("username") String username,
            @Param("password") String password,
            @Param("uri") String uri,
            @Param("databaseUsername") String databaseUsername,
            @Param("databasePassword") String databasePassword,
            @Param("databaseType") String databaseType,
            @Param("sslMode") String sslMode,
            @Param("connectionString") String connectionString,
            @Param("tableIncludeList") String tableIncludeList,
            @Param("schemaIncludeList") String schemaIncludeList,
            @Param("collectionIncludeList") String collectionIncludeList,
            @Param("updatedAt") LocalDateTime updatedAt);

    // Xóa admin
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM admins WHERE id = :id", nativeQuery = true)
    int deleteAdmin(@Param("id") UUID id);

    // Lấy admin theo ID
    @Query(value = "SELECT * FROM admins WHERE id = :id", nativeQuery = true)
    Optional<Admin> findAdminById(@Param("id") UUID id);

    // Lấy tất cả admin
    @Query(value = "SELECT * FROM admins", nativeQuery = true)
    List<Admin> findAllAdmins();

    // Lấy admin theo username
    @Query(value = "SELECT * FROM admins WHERE username = :username", nativeQuery = true)
    Optional<Admin> findAdminByUsername(@Param("username") String username);
}