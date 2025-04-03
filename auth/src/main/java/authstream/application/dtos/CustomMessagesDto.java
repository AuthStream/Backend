package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomMessagesDto {
    private UUID messageId;
    private UUID applicationId; // Chỉ cần ID của Application
    private Integer errorCode;
    private String message;
    private LocalDateTime createdAt;
}