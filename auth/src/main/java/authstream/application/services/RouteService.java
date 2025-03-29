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
import authstream.domain.entities.HttpMethod;
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
                || dto.getDescripString() == null || dto.getMethod() == null) {
            throw new IllegalArgumentException("All fields (name, route, method, protected, description) are required");
        }
        logger.info("Checking for duplicate route: {}", dto.getRoute());
        List<Route> routeCheck = routeRepository.findRouteByRoute(dto.getRoute());
        logger.info("Found {} routes with route '{}': {}", routeCheck.size(), dto.getRoute(), routeCheck);
        if (!routeCheck.isEmpty()) {
            Route existingRoute = routeCheck.get(0);
            logger.info("Duplicate route found: {}", existingRoute.getId());
            throw new IllegalArgumentException("Duplicate route found: " + existingRoute.getId());
        }

        Route route = routeMapper.toEntity(dto);
        UUID generatedId = UUID.randomUUID();
        route.setId(generatedId);

        LocalDateTime now = LocalDateTime.now();
        routeRepository.addRoute(
                route.getId(),
                route.getName(),
                route.getRoute(),
                route.getMethod(),
                route.getCheckProtected(),
                route.getDescripString(),
                now,
                now);

        Route savedRoute = routeRepository.findRouteById(generatedId);
        if (savedRoute == null) {
            throw new RuntimeException("Failed to create route with ID: " + generatedId);
        }
        logger.info("Created route: {}", savedRoute);
        return routeMapper.toDto(savedRoute);
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
        // Kiểm tra method hợp lệ
        String method = dto.getMethod();
        if (method != null) {
            try {
                HttpMethod.valueOf(method.toUpperCase()); // Chỉ kiểm tra, không dùng HttpMethod trong entity
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        routeRepository.updateRoute(
                id,
                dto.getName(),
                dto.getRoute(),
                method,
                dto.getCheckProtected(),
                dto.getDescripString(),
                now);

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
