package authstream.application.services.kv;

import java.time.Instant;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TokenEntry {
    private Message message;
    private Instant createdAt;
    private Instant expired;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Message {
        private Object body;
    }

    public long getRemainingTTL() {
        return Instant.now().until(expired, java.time.temporal.ChronoUnit.SECONDS);
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expired);
    }
}