package authstream.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.RouteDto;

import authstream.application.dtos.ApiResponse;
import authstream.application.services.RouteService;

@RestController
@RequestMapping("/routes")
public class RouteController {

    @Autowired
    private RouteService routeService;

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);

    @PostMapping
    public ResponseEntity<?> createRoute(@RequestBody RouteDto dto) {
        try {
            RouteDto createdRoute = routeService.createRoute(dto);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse("create Route Succesfully", createdRoute));
        } catch (Exception e) {
            String message = e.getMessage();
            Object data = null;
            String idStr = message.substring("Duplicate route found: ".length()).trim();
                    UUID existingId = UUID.fromString(idStr);
                    data = new RouteDto();
                    ((RouteDto) data).setId(existingId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(e.getMessage(), data));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRouteById(@PathVariable UUID id) {
        try {
            RouteDto route = routeService.getRouteById(id);
            return ResponseEntity.ok(new ApiResponse("get route succesfully", route));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllRoutes() {

        try {
            List<RouteDto> routes = routeService.getAllRoutes();
            return ResponseEntity.ok(new ApiResponse("get route successfully", routes));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateRoute(@PathVariable UUID id, @RequestBody RouteDto dto) {
        try {
            RouteDto updatedRoute = routeService.updateRoute(id, dto);
            return ResponseEntity.ok(new ApiResponse("update route successfully", updatedRoute));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("update route having error", null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable UUID id) {
        try {
            routeService.deleteRoute(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse("delete route having error", null));

        }
    }
}