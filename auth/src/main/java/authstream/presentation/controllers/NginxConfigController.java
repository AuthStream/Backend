package authstream.presentation.controllers;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.ApiResponse;
import authstream.application.services.NginxConfigService;

@RestController
@RequestMapping("/template")
public class NginxConfigController {

    private static final Logger logger = LoggerFactory.getLogger(NginxConfigController.class);
    private final NginxConfigService nginxConfigService;

    @Autowired
    public NginxConfigController(NginxConfigService nginxConfigService) {
        this.nginxConfigService = nginxConfigService;
    }

    @GetMapping(value = "/nginx.conf")
    public ResponseEntity<ApiResponse> getNginxConfig(@RequestParam("applicationId") UUID applicationId) {
        try {
            if (applicationId == null) {
                logger.warn("No applicationId provided");
                return ResponseEntity.badRequest().body(new ApiResponse(null, "ApplicationId is required"));
            }

            Map<String, String> configs = nginxConfigService.generateConfigs(applicationId);
            String nginxConfig = configs.get("nginx.conf");
            logger.info("Generated nginx.conf for applicationId: {}", applicationId);
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(nginxConfig, "get Config successfully"));
            // return ResponseEntity.status(HttpStatus.OK).body(nginxConfig);

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(null, e.getMessage()));
        } catch (IOException e) {
            logger.error("Error generating config: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null, e.getMessage()));
        }
    }

    @GetMapping(value = "/authstream.js")
    public ResponseEntity<ApiResponse> getAustreamJs(@RequestParam("applicationId") UUID applicationId) {
        try {
            if (applicationId == null) {
                logger.warn("No applicationId provided");
                return ResponseEntity.badRequest().body(new ApiResponse(null, "ApplicationId is required"));
            }

            Map<String, String> configs = nginxConfigService.generateConfigs(applicationId);
            String jsConfig = configs.get("authstream.js");
            logger.info("Generated authstream.js for applicationId: {}", applicationId);
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new ApiResponse(jsConfig, "get config successfully"));

        } catch (IllegalArgumentException e) {
            logger.error("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ApiResponse(null, e.getMessage()));
        } catch (IOException e) {
            logger.error("Error generating config: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(null, e.getMessage()));
        }
    }
}