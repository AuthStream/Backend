package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class RoleDto {
    public UUID id;
    public String name;
    public UUID groupId;
    public String permissionId;
    public String description;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public RoleDto() {
    }
}