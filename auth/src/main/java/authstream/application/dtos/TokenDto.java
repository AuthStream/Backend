package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class TokenDto {
    private UUID id;
    private Map<String, Object> body;
    private String encryptToken;
    private Long expiredDuration;
    private UUID applicationId;

    public TokenDto() {
    }
}