package authstream.domain.entities;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Method {
    private String name;
    private String method_id;
    private String application_id;
    private LocalDateTime created_at;
    
    
}
