package authstream.infrastructure.repositories;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import authstream.domain.entities.CustomMessages;

@Repository
public interface CustomMessagesRepository extends JpaRepository<CustomMessages, UUID> {

        // Create (dùng save của JpaRepository)

        // Read all
        String findAllQuery = "SELECT * FROM custom_messages";

        @Query(value = findAllQuery, nativeQuery = true)
        List<CustomMessages> findAllCustomMessages();

        // Read by ID
        String findByIdQuery = "SELECT * FROM custom_messages WHERE custom_message_id = :id";

        @Query(value = findByIdQuery, nativeQuery = true)
        CustomMessages findCustomMessageById(@Param("id") UUID id);

        // Update
        String updateQuery = "UPDATE custom_messages SET application_id = :newApplicationId, " +
                        "error_code = :newErrorCode, message = :newMessage, created_at = :newCreatedAt " +
                        "WHERE custom_message_id = :id";

        @Modifying
        @Transactional
        @Query(value = updateQuery, nativeQuery = true)
        int updateCustomMessage(
                        @Param("id") UUID id,
                        @Param("newApplicationId") UUID newApplicationId,
                        @Param("newErrorCode") Integer newErrorCode,
                        @Param("newMessage") String newMessage,
                        @Param("newCreatedAt") LocalDateTime newCreatedAt);

        // Delete
        String deleteQuery = "DELETE FROM custom_messages WHERE custom_message_id = :id";

        @Modifying
        @Transactional
        @Query(value = deleteQuery, nativeQuery = true)
        int deleteCustomMessageById(@Param("id") UUID id);

        // Check exists
        String existsQuery = "SELECT COUNT(*) > 0 FROM custom_messages WHERE custom_message_id = :id";

        @Query(value = existsQuery, nativeQuery = true)
        boolean existsById(@Param("id") UUID id);
}