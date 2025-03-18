package authstream.application.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreviewSQLResponseDto {
    private String message;
    private String affectedRows;
    
    public PreviewSQLResponseDto(String message, String affectedRows) {
        this.message = message;
        this.affectedRows = affectedRows;
    }
}