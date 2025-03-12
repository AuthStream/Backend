package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import authstream.domain.entities.Forward;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ForwardRepository extends JpaRepository<Forward, UUID> {
        String getAllForwardQuery = "SELECT * FROM forward";
        String getForwardByIdQuery = "SELECT * FROM forward WHERE method_id = :id";
        String updateForwardByIdQuery = "UPDATE forward SET name = :newName, callback_url = :callbackUrl," +
                        "application_id = :newApplicationId, method_id = :newMethodId" +
                        "domain_name = :domainName, proxy_host_ip = :proxyHostIp, created_at = :createdAt WHERE id = :id";
        String deleteForwardByIdQuery = "DELETE FROM forward WHERE method_id = :id";
        String addForwardQuery = "INSERT INTO forward ( method_id,application_id,  name,callback_url, domain_name, proxy_host_ip, created_at)"
                        +
                        "VALUES (:methodId,:applicationId, :name,  :callbackUrl, :domainName, :proxyHostIp, :createdAt)";

        @Query(value = getAllForwardQuery, nativeQuery = true)
        List<Forward> getAllForward();

        @Query(value = getForwardByIdQuery, nativeQuery = true)
        Forward getForwardById(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = updateForwardByIdQuery, nativeQuery = true)
        int updateForward(@Param("id") UUID id, @Param("newName") String newName,
                        @Param("newApplicationId") UUID newApplicationId, @Param("newMethodId") UUID newMethodId,
                        @Param("createdAt") LocalDateTime createdAt, @Param("callbackUrl") String callbackUrl,
                        @Param("domainName") String domainName, @Param("proxyHostIp") String proxyHostIp);

        @Modifying
        @Transactional
        @Query(value = deleteForwardByIdQuery, nativeQuery = true)
        int deleteForward(@Param("id") UUID id);

        @Modifying
        @Transactional
        @Query(value = addForwardQuery, nativeQuery = true)
        int addForward( @Param("methodId") UUID methodId, 
        @Param("applicationId") UUID applicationId, @Param("name") String name, 
                       @Param("callbackUrl") String callbackUrl,
                        @Param("createdAt") LocalDateTime createdAt, @Param("domainName") String domainName,
                        @Param("proxyHostIp") String proxyHostIp);

        Optional<Forward> findByApplicationId(UUID applicationId);
}
