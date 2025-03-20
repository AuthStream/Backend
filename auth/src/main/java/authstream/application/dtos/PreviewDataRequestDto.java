package authstream.application.dtos;

import java.util.List;

import authstream.application.services.db.DatabaseClass.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreviewDataRequestDto {
    private String connectionString;
    private List<Table> tables;
    private Integer limit;
    private Integer offset;
}