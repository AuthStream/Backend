package authstream.domain.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "forward")
public class Forward {

    @Id
    @Column(name = "method_id", nullable = false)
    private String methodId;

    @Column(name = "application_id", nullable = false)
    private String applicationId;

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
}
