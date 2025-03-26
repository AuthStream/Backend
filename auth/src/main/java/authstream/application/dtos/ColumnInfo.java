package authstream.application.dtos;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ColumnInfo {
    private String name;
    private String type;
    private List<String> constraints;
    private Map<String, String> referenceTo;
}