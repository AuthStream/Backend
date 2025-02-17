package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Repository;

import authstream.domain.entities.Forward;
import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;




@Repository
public interface ForwardRepository extends JpaRepository<Forward, String> {
    String getAllForwardQuery = "SELECT * FROM forward";
    String getForwardByIdQuery = "SELECT * FROM forward WHERE id = :id";
    String updateForwardByIdQuery = "UPDATE forward SET name = :newName, callback_url = :callbackUrl," +
     "application_id = :newApplicationId, method_id = :newMethodId" +
     "domain_name = :domainName, proxy_host_ip = :proxyHostIp, created_at = :createdAt WHERE id = :id";
    String deleteForwardByIdQuery = "DELETE FROM forwards WHERE id = :id";
    String addForwardQuery = "INSERT INTO forward (name, application_id, method_id, callback_url, domain_name, proxy_host_ip created_at)" +
     "VALUES (:name, :applicationId, :methodId, :callbackUrl, :domainName, :proxyHostIp, :createdAt)";

    @Query(value = getAllForwardQuery, nativeQuery = true)
    List<Forward> getAllForward(); 

    @Query(value = getForwardByIdQuery, nativeQuery = true)
    Forward getForwardById(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = updateForwardByIdQuery, nativeQuery = true)
    int updateForward(@Param("id") String id, @Param("newName") String newName,
        @Param("newApplicationId") String newApplicationId, @Param("newMethodId") String newMethodId,
        @Param("createdAt") LocalDateTime createdAt, @Param("callbackUrl") String callbackUrl,
        @Param("domainName") String domainName, @Param("proxyHostIp") String proxyHostIp);

    @Modifying
    @Transactional
    @Query(value = deleteForwardByIdQuery, nativeQuery = true)
    int deleteForward(@Param("id") String id);

    @Modifying
    @Transactional
    @Query(value = addForwardQuery, nativeQuery = true)
    int addForward(@Param("name") String name, @Param("applicationId") String applicationId,
        @Param("methodId") String methodId, @Param("callbackUrl") String callbackUrl,
        @Param("createdAt") LocalDateTime createdAt, @Param("domainName") String domainName,
        @Param("proxyHostIp") String proxyHostIp);
}

