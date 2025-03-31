package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ForwardDto {
    public UUID methodId;
    public UUID applicationId;
    public String name;
    public String proxyHostIp;
    public String domainName;
    public String callbackUrl;
    public LocalDateTime createdAt;

}