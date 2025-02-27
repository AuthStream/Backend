package authstream.application.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForwardDto {
    public String method_id;
    public String application_id;
    public String name;
    public String proxyHostIp;
    public String domainName;
    public String callbackUrl;
    public LocalDateTime createdAt;

    public ForwardDto() {
    }
}