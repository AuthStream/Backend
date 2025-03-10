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
@Table(name = "forward")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
public class Forward extends Method {

    @Id
    @Column(name = "method_id", nullable = false)
    private UUID methodId;

    @Column(name = "application_id", nullable = true)
    private UUID applicationId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "proxy_host_ip", nullable = false, length = 255)
    private String proxyHostIp;

    @Column(name = "domain_name", nullable = false, length = 255)
    private String domainName;

    @Column(name = "callback_url", nullable = false, length = 255)
    private String callbackUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Forward(
            UUID methodId,
            UUID applicationId, String name, String proxyHostIp,
            String domainName, String callbackUrl, LocalDateTime createdAt) {
        this.methodId = methodId;
        this.applicationId = applicationId;
        this.name = name;
        this.proxyHostIp = proxyHostIp;
        this.domainName = domainName;
        this.callbackUrl = callbackUrl;
        this.createdAt = createdAt;
    }

}
