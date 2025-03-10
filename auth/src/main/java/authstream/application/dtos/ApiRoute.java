package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiRoute {
    public String path;
    public List<String> method;
}