package authstream.presentation.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import authstream.application.dtos.AuthTableConfigDto;
import authstream.application.services.AuthTableConfigService;

@RestController
@RequestMapping("/auth-table-configs")
public class AuthTableConfigController {

    private final AuthTableConfigService configService;

    public AuthTableConfigController(AuthTableConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<List<AuthTableConfigDto>> getAllConfigs() {
        List<AuthTableConfigDto> configs = configService.getAllConfigs();
        return ResponseEntity.ok(configs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthTableConfigDto> getConfigById(@PathVariable UUID id) {
        AuthTableConfigDto config = configService.getConfigById(id);
        return ResponseEntity.ok(config);
    }

    @PostMapping
    public ResponseEntity<AuthTableConfigDto> createConfig(@RequestBody AuthTableConfigDto dto) {
        AuthTableConfigDto created = configService.createConfig(dto);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthTableConfigDto> updateConfig(
            @PathVariable UUID id,
            @RequestBody AuthTableConfigDto dto) {
        AuthTableConfigDto updated = configService.updateConfig(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteConfig(@PathVariable UUID id) {
        boolean checkDelete = configService.deleteConfig(id);

        if (checkDelete) {
            return ResponseEntity.ok(Map.of(
                    "message", "Config deleted successfully",
                    "success", true));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "message", "Config deletion failed",
                    "success", false));
        }
    }

}