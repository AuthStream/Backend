package authstream.presentation.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.services.NginxConfigService;

@RestController
@RequestMapping("/api/nginx-config")
public class NginxConfigController {

    private static final Logger logger = LoggerFactory.getLogger(NginxConfigController.class);
    private final NginxConfigService nginxConfigService;

    @Autowired
    public NginxConfigController(NginxConfigService nginxConfigService) {
        this.nginxConfigService = nginxConfigService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> generateNginxConfig(
            @RequestBody Map<String, UUID> requestBody) {
        try {
            UUID applicationId = requestBody.get("applicationId");
            UUID providerId = requestBody.get("providerId");

            if (applicationId == null && providerId == null) {
                logger.warn("No applicationId or providerId provided in request body");
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Either applicationId or providerId is required"));
            }

            Map<String, String> configs = nginxConfigService.generateConfigs(applicationId, providerId);
            logger.info("Generated configs for applicationId: {} or providerId: {}", applicationId, providerId);
            return ResponseEntity.ok(configs);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IOException e) {
            logger.error("Error generating config: {}", e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Failed to generate config: " + e.getMessage()));
        }
    }
}