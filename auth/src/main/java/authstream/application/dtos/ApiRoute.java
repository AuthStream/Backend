package authstream.application.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiRoute {
    public String path;
    public String method;
}