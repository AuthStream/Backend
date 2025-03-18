package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreviewSQLRequestDto {
    private String connectionString;
    private String query;
}