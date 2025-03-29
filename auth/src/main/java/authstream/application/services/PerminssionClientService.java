package authstream.application.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import authstream.application.dtos.RouteDto;
import authstream.application.services.kv.TokenEntry;
import authstream.application.services.kv.TokenStoreService;
import io.jsonwebtoken.Claims;

@Service
public class PerminssionClientService {

    private static final Logger logger = LoggerFactory.getLogger(PerminssionClientService.class);

    private final RouteService routeService;

    public PerminssionClientService(RouteService routeService) {
        this.routeService = routeService;
    }

    private static final String JWT_SECRET = "mySuperSecretKey1234567890123456";

    private TokenStoreService tokenStoreService;

    public Map<String, Object> checkPermission(String originalUri, String originalMethod,
            String authHeader, Map<String, Object> requestBody) {
        try {

            boolean isProtected = isProtectedRoute(originalUri, originalMethod);

            if (!isProtected) {
                logger.info("Route {} ({}) is unprotected, forwarding directly", originalUri, originalMethod);
                requestBody.put("auth_status", "unprotected_route");
                requestBody.put("action", "forward");
                return requestBody;
            }

            logger.info("Route {} ({}) is protected, checking token", originalUri, originalMethod);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                logger.warn("No valid token provided for protected route: {}", originalUri);
                requestBody.put("auth_status", "no_authentication_provided");
                requestBody.put("action", "deny");
                requestBody.put("reason", "Missing or invalid Authorization header");
                return requestBody;
            }

            String token = authHeader.substring("Bearer ".length()).trim();
            Claims claims = validateAndParseToken(token);

            if (claims == null) {
                logger.warn("Invalid or expired token for route: {}", originalUri);
                requestBody.put("auth_status", "invalid_token");
                requestBody.put("action", "deny");
                requestBody.put("reason", "Token is invalid or expired");
                return requestBody;
            }

            @SuppressWarnings("unchecked")
            Set<String> permissions = claims.get("permissions", Set.class);
            String requiredPermission = getRequiredPermission(originalUri, originalMethod);

            if (permissions != null && permissions.contains(requiredPermission)) {
                logger.info("User has permission '{}' for route: {}", requiredPermission, originalUri);
                requestBody.put("auth_status", "authorized");
                requestBody.put("action", "allow");
                requestBody.put("permissions", permissions);
            } else {
                logger.warn("User lacks permission '{}' for route: {}", requiredPermission, originalUri);
                requestBody.put("auth_status", "unauthorized");
                requestBody.put("action", "deny");
                requestBody.put("reason", "Insufficient permissions");
            }

            return requestBody;

        } catch (Exception e) {
            logger.error("Error checking permission for URI {}: {}", originalUri, e.getMessage());
            requestBody.put("auth_status", "error");
            requestBody.put("action", "deny");
            requestBody.put("reason", "Server error: " + e.getMessage());
            return requestBody;
        }
    }

    /**
     * Giải mã và kiểm tra token
     */
    private Claims validateAndParseToken(String token) {
        try {
            // Bước 1: Kiểm tra token trong TokenStoreService
            if (!TokenStoreService.existsAndValid(token)) {
                logger.warn("Token {} not found or expired in store", token);
                return null;
            }

            // Bước 2: Lấy TokenEntry từ store
            TokenEntry tokenEntry = TokenStoreService.read(token);
            if (tokenEntry == null) {
                logger.warn("Token {} entry not found after validation", token);
                return null;
            }

            // Bước 3: Giải mã token JWT để lấy Claims
            // Claims claims = Jwts.parser()
            // .setSigningKey(JWT_SECRET.getBytes()) // Phải khớp với secret trong
            // JwtService
            // .parseClaimsJws(token)
            // .getBody();

            // Optional: So sánh claims từ JWT với TokenEntry.Message nếu cần
            // Object storedClaims = tokenEntry.getMessage().getBody();
            // if (!claims.equals(storedClaims)) {
            // logger.warn("Claims mismatch between JWT and stored entry for token {}",
            // token);
            // return null;
            // }

            return null;

        } catch (Exception e) {
            logger.error("Token validation failed for {}: {}", token, e.getMessage());
            return null;
        }
    }

    // private boolean isProtectedRoute(String uri, String method) {

    //     List<RouteDto> routeList = routeService.getAllRoutes();

    //     logger.info("check routeList", routeList);

    //     if (routeList.contains(uri)) {
    //         return true;
    //     } else {
    //         return false;
    //     }
    // }

    private boolean isProtectedRoute(String originalUri, String originalMethod) {
        // Lấy danh sách tất cả các route từ RouteService
        List<RouteDto> routeList = routeService.getAllRoutes();
        logger.info("Checking routeList for URI: {} and Method: {}. Route list: {}",
                originalUri, originalMethod, routeList);

        if (routeList == null || routeList.isEmpty()) {
            logger.warn("No routes found in the system. Treating URI: {} as unprotected.", originalUri);
            return false;
        }

        String method = originalMethod != null ? originalMethod.toUpperCase() : "";

        for (RouteDto route : routeList) {
            String routePath = route.getRoute();
            String routeMethod = route.getMethod() != null ? route.getMethod().toUpperCase() : "";

            if (!method.equals(routeMethod)) {
                continue; 
            }

            if (isRouteMatch(originalUri, routePath)) {
                boolean isProtected = route.getCheckProtected();
                logger.info("Found matching route: {} (Method: {}). Protected: {}",
                        routePath, routeMethod, isProtected);
                return isProtected;
            }
        }

        logger.info("No matching route found for URI: {} (Method: {}). Treating as unprotected.",
                originalUri, method);
        return false;
    }


    private boolean isRouteMatch(String originalUri, String routePath) {
        if (originalUri == null || routePath == null) {
            return false;
        }

        String[] uriParts = originalUri.split("/");
        String[] routeParts = routePath.split("/");

        if (uriParts.length != routeParts.length) {
            return false;
        }

        for (int i = 0; i < routeParts.length; i++) {
            String routePart = routeParts[i];
            String uriPart = uriParts[i];

            if (routePart.startsWith(":")) {
                continue;
            }

            if (!routePart.equals(uriPart)) {
                return false;
            }
        }

        return true;
    }

    private String getRequiredPermission(String uri, String method) {
        String resource = uri.split("/")[2];
        String action = method.toLowerCase().equals("get") ? "read" : "write";
        return action + ":" + resource;
    }
}