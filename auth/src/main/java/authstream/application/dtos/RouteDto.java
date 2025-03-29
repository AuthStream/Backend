package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class RouteDto {

    private UUID id;
    private String name;
    private String route;
    private String method; // Đổi thành String để khớp JSON
    private Boolean checkProtected;
    private String descripString;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}