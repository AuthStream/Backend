package authstream.domain.entities;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Table(name = "applications")
@Getter
@Setter
@ToString
@NoArgsConstructor

public class Application {
    
    @Id
    @Column(name = "application_id", nullable = false)
    private String id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(name = "admin_id", nullable = false)
    private String adminId;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = true, unique = true)
    private Provider provider;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    
    public Application(String id, String name, String adminId, Provider provider, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.provider = provider;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    public Application(String id, String name, String adminId, Provider provider) {
        this.id = id;
        this.name = name;
        this.adminId = adminId;
        this.provider = provider;
    }
    

}
