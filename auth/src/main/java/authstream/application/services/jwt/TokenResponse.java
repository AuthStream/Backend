package authstream.application.services.jwt;

import authstream.application.services.kv.TokenEntry;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class TokenResponse {
    private String token;
    private TokenEntry tokenEntry;
}
