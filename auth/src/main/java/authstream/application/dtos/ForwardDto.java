package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForwardDto {
    public UUID method_id;
    public UUID application_id;
    public String name;
    public String proxyHostIp;
    public String domainName;
    public String callbackUrl;
    public LocalDateTime createdAt;

    public ForwardDto() {
    }
}