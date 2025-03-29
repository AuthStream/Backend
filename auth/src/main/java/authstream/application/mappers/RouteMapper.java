package authstream.application.mappers;

import org.springframework.stereotype.Component;

import authstream.application.dtos.RouteDto;
import authstream.domain.entities.Route;

@Component
public class RouteMapper {

    public Route toEntity(RouteDto dto) {
        Route route = new Route();
        route.setName(dto.getName());
        route.setRoute(dto.getRoute());
        route.setMethod(dto.getMethod());
        route.setCheckProtected(dto.getCheckProtected());
        route.setDescripString(dto.getDescripString());
        route.setCreatedAt(dto.getCreatedAt());
        route.setUpdatedAt(dto.getUpdatedAt());
        return route;
    }

    public RouteDto toDto(Route route) {
        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setRoute(route.getRoute());
        dto.setMethod(route.getMethod()); 
        dto.setCheckProtected(route.getCheckProtected());
        dto.setDescripString(route.getDescripString());
        dto.setCreatedAt(route.getCreatedAt());
        dto.setUpdatedAt(route.getUpdatedAt());
        return dto;
    }
}