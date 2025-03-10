package authstream.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "groups")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Group {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "role_id", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String roleId;

    @Column(name = "descriptions", length = 1000)
    private String descriptions;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Group(
            UUID id, String name, String roleId, String descriptions, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.roleId = roleId;
        this.descriptions = descriptions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}