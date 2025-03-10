package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {
    public UUID id;
    public String username;
    public String password;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public UserDto() {
    }

}