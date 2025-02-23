package authstream.application.dtos;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplicationDto {
    public String id;
    public String name;
    public String adminId;
    public String providerId;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

    public ApplicationDto() {
    }
}