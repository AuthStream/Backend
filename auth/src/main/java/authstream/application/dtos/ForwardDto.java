package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public class ForwardDto {
    public UUID methodId;
    public UUID applicationId;
    public String name;
    public String proxyHostIp;
    public String domainName;
    public String callbackUrl;
    public LocalDateTime createdAt;

    public ForwardDto() {}
}