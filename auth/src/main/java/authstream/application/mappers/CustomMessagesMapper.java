package authstream.application.mappers;

import java.time.LocalDateTime;

import authstream.application.dtos.CustomMessagesDto;
import authstream.domain.entities.Application;
import authstream.domain.entities.CustomMessages;

public class CustomMessagesMapper {

    public static CustomMessages toEntity(CustomMessagesDto dto) {
        if (dto == null)
            return null;

        CustomMessages message = new CustomMessages();
        message.setMessageId(dto.getMessageId());
        message.setErrorCode(dto.getErrorCode());
        message.setMessage(dto.getMessage());
        message.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());

        // Gán Application (chỉ cần ID, sẽ set trong service)
        if (dto.getApplicationId() != null) {
            Application application = new Application();
            application.setId(dto.getApplicationId());
            message.setApplication(application);
        } else {
            message.setApplication(null);
        }
        return message;
    }

    public static CustomMessagesDto toDto(CustomMessages entity) {
        if (entity == null)
            return null;

        CustomMessagesDto dto = new CustomMessagesDto();
        dto.setMessageId(entity.getMessageId());
        dto.setApplicationId(entity.getApplication() != null ? entity.getApplication().getId() : null);
        dto.setErrorCode(entity.getErrorCode());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());

        return dto;
    }
}