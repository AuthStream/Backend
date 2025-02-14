package authstream.domain.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @Column(name = "application_id", nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, unique = true)
    private Provider provider;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

}
