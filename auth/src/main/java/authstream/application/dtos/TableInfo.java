package authstream.application.dtos;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TableInfo {
    private String tableName;
    private String schema;
    private List<ColumnInfo> columns;
}