package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDto {
    public UUID id;
    public String name;
    public UUID adminId; 
    public UUID providerId; 
    public UUID tokenId; 
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ApplicationDto() {
    }
}