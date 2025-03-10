package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class GroupDto {
    public UUID id;
    public String name;
    public String roleId;
    public String descriptions;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public GroupDto() {
    }
}