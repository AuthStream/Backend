package authstream.domain.entities;

import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
public class Token {
    @Id
    @Column(name = "token_id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> body;

    @Column(name = "encrypt_token", nullable = false)
    private String encryptToken;

    @Column(name = "expired_duration", nullable = false)
    private Long expiredDuration;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "application_id", referencedColumnName = "application_id", nullable = true, unique = true)
    private Application application;

    public Token(UUID id, Map<String, Object> body, String encryptToken, Long expiredDuration,
            Application application) {
        this.id = id;
        this.body = body;
        this.encryptToken = encryptToken;
        this.expiredDuration = expiredDuration;
        this.application = application;
    }
}