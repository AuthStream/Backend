package authstream.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "custom_messages")
public class CustomMessages {

    @Id
    @Column(name = "custom_message_id", nullable = false)
    private UUID messageId;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "application_id")
    private Application application;

    @Column(name = "error_code", nullable = false)
    private Integer errorCode;

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
