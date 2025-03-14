package authstream.application.mappers;

import authstream.application.dtos.UserDto;
import authstream.domain.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        // dto.setPassword(user.getPassword());
        dto.createdAt = user.getCreatedAt();
        dto.updatedAt = user.getUpdatedAt();
        return dto;
    }

    public User toEntity(UserDto dto) {
        if (dto == null) {
            return null;
        }
        User user = new User();
        user.setId(dto.getId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());

        return user;
    }
}