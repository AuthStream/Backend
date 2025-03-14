package authstream.infrastructure.repositories;

import authstream.domain.entities.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import java.util.UUID;
import java.util.List;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    String addTokenQuery = "INSERT INTO tokens (token_id, body, encrypt_token, expired_duration, application_id) " +
            "VALUES (:id, CAST(:body AS jsonb), :encryptToken, :expiredDuration, :applicationId)";
    String getAllTokenQuery = "SELECT * FROM tokens";
    String getTokenByIdQuery = "SELECT * FROM tokens WHERE token_id = :id";

    @Modifying
    @Transactional
    @Query(value = addTokenQuery, nativeQuery = true)
    int addToken(@Param("id") UUID id,
            @Param("body") String body,
            @Param("encryptToken") String encryptToken,
            @Param("expiredDuration") Long expiredDuration,
            @Param("applicationId") UUID applicationId);

    @Query(value = getAllTokenQuery, nativeQuery = true)
    List<Token> getAllToken();

    @Query(value = getTokenByIdQuery, nativeQuery = true)
    Token getTokenById(@Param("id") UUID id);

}