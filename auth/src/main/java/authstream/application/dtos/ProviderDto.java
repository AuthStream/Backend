package authstream.application.dtos;

import authstream.domain.entities.ProviderType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ProviderDto {
    public String id;
    public String applicationId; // Optional
    public String methodId; // Optional
    public ProviderType type;
    public String name;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ProviderDto() {
    }
}