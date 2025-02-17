package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import authstream.domain.entities.Application;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




@Repository
public interface ApplicationRepository extends JpaRepository<Application, String> {
    String getAllAppQuery = "SELECT * FROM applications";
    String getAppByIdQuery = "SELECT * FROM applications WHERE id = :id";
    String updateAppByIdQuery = "UPDATE applications SET name = :newName," +
     "provider_id = :newProviderId, admin_id = :newAdminId, updated_at = :newUpdatedAt WHERE id = :id";
    String deleteAppByIdQuery = "DELETE FROM applications WHERE id = :id";
    String addAppQuery = "INSERT INTO applications (name, provider_id, admin_id, created_at, updated_at)" +
     "VALUES (:name, :providerId, :adminId, :createdAt, :updatedAt)";

    @Query(value = getAllAppQuery, nativeQuery = true)
    List<Application> getAllApplications(); 

    @Query(value = getAppByIdQuery, nativeQuery = true)
    Application getAppById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = updateAppByIdQuery, nativeQuery = true)
    int updateApplication(@Param("id") String id, @Param("newName") String newName,
     @Param("newProviderId") String newProviderId, @Param("newAdminId") String newAdminId, @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

    @Modifying
    @Transactional
    @Query(value = deleteAppByIdQuery, nativeQuery = true)
    int deleteApplication(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = addAppQuery, nativeQuery = true)
    int addApplication(@Param("name") String name, @Param("providerId") String providerId,
     @Param("adminId") String adminId, @Param("createdAt") LocalDateTime createdAt, @Param("updatedAt") LocalDateTime updatedAt);
}
