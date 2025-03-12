
package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import authstream.domain.entities.ProviderType;

public class ProviderDto {
    public UUID id;
    public UUID applicationId;
    public UUID methodId;
    public ProviderType type;
    public String name;
    public String methodName;
    public String proxyHostIp;
    public String domainName;
    public String callbackUrl;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ProviderDto() {
    }
}