package authstream.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "custom_messages")
public class CustomMessages {

    @Id
    @Column(name = "custom_message_id", nullable = false)
    private String messageId;

    @ManyToOne
    @JoinColumn(name = "application_id", referencedColumnName = "application_id", nullable = false)
    private Application application;

    @Column(name = "error_code", nullable = false)
    private Integer errorCode;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
