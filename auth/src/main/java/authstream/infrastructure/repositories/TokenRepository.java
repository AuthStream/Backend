package authstream.infrastructure.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import authstream.domain.entities.Token;
import jakarta.transaction.Transactional;

@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    String addTokenQuery = "INSERT INTO tokens (token_id, name, body, encrypt_token, expired_duration, application_id) "
            +
            "VALUES (:id, :name, CAST(:body AS jsonb), :encryptToken, :expiredDuration, :applicationId)";
    String getAllTokenQuery = "SELECT * FROM tokens";
    String getTokenByIdQuery = "SELECT * FROM tokens WHERE token_id = :id";
    String deleteToken = "DELETE FROM tokens WHERE token_id = :id";

    @Modifying
    @Transactional
    @Query(value = addTokenQuery, nativeQuery = true)
    int addToken(@Param("id") UUID id,
            @Param("name") String name,
            @Param("body") String body,
            @Param("encryptToken") String encryptToken,
            @Param("expiredDuration") Long expiredDuration,
            @Param("applicationId") UUID applicationId);

    @Query(value = getAllTokenQuery, nativeQuery = true)
    List<Token> getAllTokens();

    @Query(value = getTokenByIdQuery, nativeQuery = true)
    Token getTokenById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query(value = deleteToken, nativeQuery = true)
    int deleteTokenById(@Param("id") UUID id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tokens SET application_id = NULL WHERE application_id = :applicationId", nativeQuery = true)
    int updateApplicationIdToNull(@Param("applicationId") UUID applicationId);

}