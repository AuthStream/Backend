package authstream.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import authstream.application.services.hashing.HashingType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auth_table_config")
@Getter
@Setter
@NoArgsConstructor
public class AuthTableConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_table", nullable = false)
    private String userTable;

        @Column(name = "username_attribute", nullable = false)
    private String usernameAttribute;

    @Column(name = "password_attribute", nullable = false)
    private String passwordAttribute;

    @Enumerated(EnumType.STRING)
    @Column(name = "hashing_type", nullable = false)
    private HashingType hashingType;

    @Column(name = "salt")
    private String salt;

    @Column(name = "hash_config", columnDefinition = "jsonb")
    private String hashConfig;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
