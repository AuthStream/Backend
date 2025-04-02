package authstream.application.dtos;

import java.util.Map;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenDto {
    private UUID id;
    private String name;
    private Map<String, Object> body;
    private String encryptToken;
    private Long expiredDuration;
    private UUID applicationId;

    public TokenDto() {
    }
}