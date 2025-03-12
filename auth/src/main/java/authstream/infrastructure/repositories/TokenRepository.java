package authstream.infrastructure.repositories;

import authstream.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO tokens (token_id, body, encrypt_token, expired_duration, application_id) " +
                   "VALUES (:id, CAST(:body AS jsonb), :encryptToken, :expiredDuration, :applicationId)", 
           nativeQuery = true)
    int addToken(@Param("id") UUID id,
                 @Param("body") String body,
                 @Param("encryptToken") String encryptToken,
                 @Param("expiredDuration") Long expiredDuration,
                 @Param("applicationId") UUID applicationId);
}