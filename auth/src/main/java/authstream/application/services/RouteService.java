package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import authstream.application.dtos.RouteDto;
import authstream.application.mappers.RouteMapper;
import authstream.domain.entities.Route;
import authstream.infrastructure.repositories.RouteRepository;

@Service
public class RouteService {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteMapper routeMapper;

    @Transactional
    public RouteDto createRoute(RouteDto dto) {
        if (dto.getName() == null || dto.getRoute() == null || dto.getCheckProtected() == null
                || dto.getDescripString() == null) {
            throw new IllegalArgumentException("All fields (name, route, protected, description) are required");
        }
        logger.info("Checking for duplicate route: {}", dto.getRoute());
        List<Route> routeCheck = routeRepository.findRouteByRoute(dto.getRoute());
        logger.info("Found {} routes with route '{}': {}", routeCheck.size(), dto.getRoute(), routeCheck);
        if (!routeCheck.isEmpty()) {
        
            logger.info("vai loi nay", routeCheck);

            Route existingRouteId = routeCheck.get(0);
            
            throw new IllegalArgumentException(
                    "Duplicate route found:" + existingRouteId.getId());
        }

        UUID id = dto.getId() != null ? dto.getId() : UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        routeRepository.addRoute(
                id,
                dto.getName(),
                dto.getRoute(),
                dto.getCheckProtected(),
                dto.getDescripString(),
                now,
                now);
        Route route = routeRepository.findRouteById(id);
        logger.info("Created route: {}", route);
        return routeMapper.toDto(route);
    }

    public RouteDto getRouteById(UUID id) {
        Route route = routeRepository.findRouteById(id);
        if (route == null) {
            throw new RuntimeException("Route not found with id: " + id);
        }
        return routeMapper.toDto(route);
    }

    public List<RouteDto> getAllRoutes() {
        List<Route> routes = routeRepository.findAllRoutes();
        return routes.stream().map(routeMapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public RouteDto updateRoute(UUID id, RouteDto dto) {
        Route existingRoute = routeRepository.findRouteById(id);
        if (existingRoute == null) {
            throw new RuntimeException("Route not found with id: " + id);
        }
        routeRepository.updateRoute(
                id,
                dto.getName(),
                dto.getRoute(),
                dto.getCheckProtected(),
                dto.getDescripString(),
                LocalDateTime.now());
        Route updatedRoute = routeRepository.findRouteById(id);
        logger.info("Updated route: {}", updatedRoute);
        return routeMapper.toDto(updatedRoute);
    }

    @Transactional
    public void deleteRoute(UUID id) {
        Route route = routeRepository.findRouteById(id);
        if (route == null) {
            throw new RuntimeException("Route not found with id: " + id);
        }
        routeRepository.deleteRoute(id);
        logger.info("Deleted route with id: {}", id);
    }
}
