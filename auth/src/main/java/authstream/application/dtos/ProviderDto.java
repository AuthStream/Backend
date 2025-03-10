package authstream.application.dtos;

import authstream.domain.entities.ProviderType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProviderDto {
    public UUID id;
    public UUID applicationId;
    public UUID methodId;
    public ProviderType type;
    public String name;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ProviderDto() {
    }
}