package authstream.domain.entities;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.Map;
import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @Column(name = "token_id", nullable = false)
    private String id;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> body;

    @Column(name = "encrypt_token", nullable = false)
    private String encryptToken;

    // In seconds
    @Column(name = "expired_duration", nullable = false)
    private Long expiredDuration;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id", referencedColumnName = "application_id", nullable = false, unique = true)
    private Application application;
}
