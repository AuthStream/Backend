package authstream.application.mappers;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import authstream.application.dtos.RouteDto;
import authstream.domain.entities.Route;

@Component
public class RouteMapper {

    public RouteDto toDto(Route route) {
        if (route == null) {
            return null;
        }
        RouteDto dto = new RouteDto();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setRoute(route.getRoute());
        dto.setCheckProtected(route.getCheckProtected());
        dto.setDescripString(route.getDescripString());
        dto.setCreatedAt(route.getCreatedAt());
        dto.setUpdatedAt(route.getUpdatedAt());
        return dto;
    }

    public Route toEntity(RouteDto dto) {
        if (dto == null) {
            return null;
        }
        Route route = new Route();
        route.setId(dto.getId());
        route.setName(dto.getName());
        route.setRoute(dto.getRoute());
        route.setCheckProtected(dto.getCheckProtected());
        route.setDescripString(dto.getDescripString());
        route.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        route.setUpdatedAt(dto.getUpdatedAt() != null ? dto.getUpdatedAt() : LocalDateTime.now());
        return route;
    }
}