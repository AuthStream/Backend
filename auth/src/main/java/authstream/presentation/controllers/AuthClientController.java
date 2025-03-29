package authstream.presentation.controllers;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.ApiResponse;
import authstream.application.services.AuthService;
import authstream.application.services.RouteService;

@RestController
@RequestMapping("/authstream")
public class AuthClientController {

    private static final Logger logger = LoggerFactory.getLogger(RouteService.class);
    private final AuthService authService;

    public AuthClientController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @RequestBody Map<String, Object> requestBody,
            @RequestHeader(value = "Authorization", required = false) String token) {
        System.out.println("Header Authorization received in controller: " + token);
        String username = (String) requestBody.get("username");
        String password = (String) requestBody.get("password");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Lấy UUID sau "Bearer "
        }

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username and password required"));
        }

        try {
            Map<String, Object> tokenInfo = authService.login(username, password, token);
            return ResponseEntity.ok(tokenInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestParam String token) {
        try {
            Map<String, Object> tokenInfo = authService.validateToken(token);
            return ResponseEntity.ok(tokenInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        }
    }

        @PostMapping("/permissioncheck")
        public ResponseEntity<ApiResponse> checkPermission(
                @RequestHeader(value = "X-Original-URI", required = true) String originalUri,
                @RequestHeader(value = "X-Original-Method", required = true) String originalMethod,
                @RequestHeader(value = "Authorization", required = false) String authHeader,
                @RequestBody Map<String, Object> requestBody) {

            try {

                if (requestBody == null) {
                    requestBody = new HashMap<>();
                }

                logger.info("Received checkPermission request: URI={}, Method={}, Auth={}, Body={}",
                        originalUri, originalMethod, authHeader, requestBody);

                //check protected route
                        
                //check token
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    Map<String, String> authstrema_data = new HashMap<>();

                } else {
                requestBody.put("auth_data", "no_authentication_provided");
                }

                //map message
                String message = "Permission check received for URI: " + originalUri;
                return ResponseEntity.ok(new ApiResponse(requestBody,message ));

            } catch (Exception e) {
                logger.error("Error processing request: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse("Server error: " + e.getMessage(), null));
            }
        }
}
