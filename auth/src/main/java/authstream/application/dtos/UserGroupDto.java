package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class UserGroupDto {
    public UUID userId;
    public UUID groupId;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public UserGroupDto() {
    }
}