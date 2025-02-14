package authstream.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "providers")
public class Provider {

    @Id
    @Column(name= "provider_id",nullable = false)
    private String id;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

    @Column(name = "method_id", nullable = false)
    private String methodId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProviderType type;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "provider", cascade = CascadeType.ALL, orphanRemoval = true)
    private Application application;
}
