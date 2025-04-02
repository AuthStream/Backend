package authstream.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Role {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "group_id", nullable = true)

    private UUID groupId;

    @Column(name = "permission_id", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private String permissionId;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Role(
            UUID id, String name,
            UUID groupId, String permissionId, String description,
            LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.groupId = groupId;
        this.permissionId = permissionId;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}