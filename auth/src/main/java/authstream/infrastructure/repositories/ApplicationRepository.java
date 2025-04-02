
package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.Application;
import jakarta.transaction.Transactional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
        String getAllAppQuery = "SELECT * FROM applications";
        String getAppByIdQuery = "SELECT * FROM applications WHERE application_id = :id";
        String updateAppByIdQuery = "UPDATE applications SET name = :newName, " +
                        "provider_id = :newProviderId, admin_id = :newAdminId, token_id = :newTokenId, updated_at = :newUpdatedAt "
                        +
                        "WHERE application_id = :id";
        String deleteAppByIdQuery = "DELETE FROM applications WHERE application_id = :id";
        String addAppQuery = "INSERT INTO applications (application_id, name, provider_id, admin_id, token_id, created_at, updated_at) "
                        +
                        "VALUES (:id, :name, :providerId, :adminId, :tokenId, :createdAt, :updatedAt)";

        @Query(value = getAllAppQuery, nativeQuery = true)
        List<Application> getAllApplications();

        @Query(value = getAppByIdQuery, nativeQuery = true)
        Application getAppById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = updateAppByIdQuery, nativeQuery = true)
        int updateApplication(
                        @Param("id") UUID id,
                        @Param("newName") String newName,
                        @Param("newProviderId") UUID newProviderId,
                        @Param("newAdminId") UUID newAdminId,
                        @Param("newTokenId") UUID newTokenId,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt);

        @Modifying
        @Transactional
        @Query(value = deleteAppByIdQuery, nativeQuery = true)
        int deleteApplication(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addAppQuery, nativeQuery = true)
        int addApplication(@Param("id") UUID id, @Param("name") String name, @Param("providerId") UUID providerId,
                        @Param("adminId") UUID adminId, @Param("tokenId") UUID tokenId,
                        @Param("createdAt") LocalDateTime createdAt, @Param("updatedAt") LocalDateTime updatedAt);

        @Modifying
        @Transactional
        @Query(value = "UPDATE applications SET provider_id = NULL WHERE provider_id = :providerId", nativeQuery = true)
        void updateProviderIdToNull(@Param("providerId") UUID providerId);

}