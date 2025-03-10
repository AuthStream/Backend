package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PermissionDto {
    public UUID id;
    public String name;
    public String apiRoutes;
    public String description;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public PermissionDto() {
    }
}