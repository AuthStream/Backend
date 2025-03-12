package authstream.domain.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Table(name = "providers")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Provider {

    @Id
    @Column(name = "provider_id", nullable = false)
    private UUID id;

    @Column(name = "application_id", nullable = true)
    private UUID applicationId;

    @Column(name = "method_id", nullable = false)
    private UUID methodId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType type;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Provider(
            UUID id,
            UUID applicationId,
            UUID methodId, ProviderType type, String name,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.applicationId = applicationId;
        this.methodId = methodId;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}
