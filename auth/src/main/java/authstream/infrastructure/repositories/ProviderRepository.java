package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import authstream.domain.entities.Provider;
import authstream.domain.entities.ProviderType;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




@Repository
public interface ProviderRepository extends JpaRepository<Provider, String> {
    String getAllProviderQuery = "SELECT * FROM providers";
    String getProviderByIdQuery = "SELECT * FROM providers WHERE id = :id";
    String updateProviderByIdQuery = "UPDATE providers SET name = :newName," +
     "application_id = :newApplicationId, method_id = :newMethodId, type = :type, updated_at = :newUpdatedAt WHERE id = :id";
    String deleteProviderByIdQuery = "DELETE FROM providers WHERE id = :id";
    String addProviderQuery = "INSERT INTO providers (name, application_id, method_id, type, created_at, updated_at)" +
     "VALUES (:name, :applicationId, :methodId, :type, :createdAt, :updatedAt)";

    @Query(value = getAllProviderQuery, nativeQuery = true)
    List<Provider> getAllProviders(); 

    @Query(value = getProviderByIdQuery, nativeQuery = true)
    Provider getProviderById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = updateProviderByIdQuery, nativeQuery = true)
    int updateProvider(@Param("id") String id, @Param("newName") String newName,
        @Param("newApplicationId") String newApplicationId, @Param("newMethodId") String newMethodId,
        @Param("newUpdatedAt") LocalDateTime newUpdatedAt, @Param("type") ProviderType type);

    @Modifying
    @Transactional
    @Query(value = deleteProviderByIdQuery, nativeQuery = true)
    int deleteProvider(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = addProviderQuery, nativeQuery = true)
    int addProvider(@Param("name") String name, @Param("applicationId") String applicationId,
        @Param("methodId") String methodId, @Param("type") ProviderType type,
        @Param("createdAt") LocalDateTime createdAt, @Param("updatedAt") LocalDateTime updatedAt);
}

