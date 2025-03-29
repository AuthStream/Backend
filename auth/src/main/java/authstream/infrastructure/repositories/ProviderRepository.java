
package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.Provider;
import jakarta.transaction.Transactional;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, UUID> {
        String getAllProviderQuery = "SELECT * FROM providers";
        String getProviderByIdQuery = "SELECT * FROM providers WHERE provider_id = :id";
        String updateProviderByIdQuery = "UPDATE providers SET name = :newName, " +
                        "application_id = :newApplicationId, method_id = :newMethodId, type = :type, updated_at = :newUpdatedAt WHERE provider_id = :id";
        String deleteProviderByIdQuery = "DELETE FROM providers WHERE provider_id = :id";
        String addProviderQuery = "INSERT INTO providers (provider_id, name, application_id, method_id, type, created_at, updated_at) "
                        +
                        "VALUES (:id, :name, :applicationId, :methodId, :type, :createdAt, :updatedAt)";
        String deleteProviderByMethodIdQuery = "DELETE FROM providers WHERE method_id = :id";
        

        @Query(value = getAllProviderQuery, nativeQuery = true)
        List<Provider> getAllProviders();

        @Query(value = getProviderByIdQuery, nativeQuery = true)
        Provider getProviderById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = updateProviderByIdQuery, nativeQuery = true)
        int updateProvider(@Param("id") UUID id, @Param("newName") String newName,
                        @Param("newApplicationId") UUID newApplicationId, @Param("newMethodId") UUID newMethodId,
                        @Param("newUpdatedAt") LocalDateTime newUpdatedAt, @Param("type") String type);

        @Modifying
        @Transactional
        @Query(value = deleteProviderByIdQuery, nativeQuery = true)
        int deleteProvider(@Param("id") UUID id);


        @Modifying
        @Transactional
        @Query(value = deleteProviderByMethodIdQuery, nativeQuery = true)
        int deleteProviderByMethodId(@Param("id") UUID id);


        @Modifying
        @Transactional
        @Query(value = addProviderQuery, nativeQuery = true)
        int addProvider(@Param("id") UUID id, @Param("name") String name, @Param("applicationId") UUID applicationId,
                        @Param("methodId") UUID methodId, @Param("type") String type,
                        @Param("createdAt") LocalDateTime createdAt, @Param("updatedAt") LocalDateTime updatedAt);
}