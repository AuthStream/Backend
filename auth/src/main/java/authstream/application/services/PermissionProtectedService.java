package authstream.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.dtos.AdminDto;
import authstream.application.dtos.AuthTableConfigDto;
import authstream.application.dtos.RouteDto;

@Service
public class PermissionProtectedService {
    private static final Logger logger = LoggerFactory.getLogger(PerminssionClientService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RouteService routeService;

    public PermissionProtectedService(RouteService routeService) {
        this.routeService = routeService;
    }

    // public Pair<String, Object> extractUsernameFromTokenBody(Object tokenBody,
    // List<AdminDto> admins) {
    // Map<String, Object> tokenData;
    // if (tokenBody instanceof String) {
    // try {
    // tokenData = objectMapper.readValue(tokenBody.toString(), Map.class);
    // } catch (JsonProcessingException e) {
    // logger.warn("Failed to parse tokenBody as JSON: {}. Treating as plain
    // string.", tokenBody);
    // tokenData = new HashMap<>(Map.of("raw", tokenBody));
    // }
    // } else {
    // tokenData = new HashMap<>(Map.of("raw", tokenBody));
    // }
    // for (AdminDto admin : admins) {

    // String databaseUsername = admin.getDatabaseUsername();
    // if (tokenData.containsKey(databaseUsername)) {
    // return Pair.of(tokenData.get(databaseUsername).toString(), null);
    // } else if (tokenBody != null &&
    // tokenBody.toString().equals(databaseUsername)) {
    // return Pair.of(databaseUsername, null);
    // }
    // }

    // return Pair.of(null, "Not found username in token"); // Không tìm thấy
    // username khớp
    // }

    public Pair<String, Object> extractUsernameFromTokenBody(Object tokenBody, AuthTableConfigDto authConfig) {
        if (tokenBody == null) {
            logger.warn("Token body is null");
            return Pair.of(null, "Token body is null");
        }

        if (authConfig == null) {
            logger.warn("Config is null");
            return Pair.of(null, "No config data provided");
        }

        logger.info("fuckings config: {}", authConfig.getUsernameAttribute());
        String usernameAttribute = authConfig.getUsernameAttribute();
        if (usernameAttribute == null) {
            logger.warn("Username attribute is null in config");
            return Pair.of(null, "Invalid config data");
        }
        logger.info("oke bo may da lay ra dung la: {}", authConfig.getUsernameAttribute());
        logger.info("ditme tokenbody:{}", tokenBody.toString());
        if (tokenBody instanceof String) {
            try {
                logger.info("ditme tokenbody: {}", tokenBody);
                Map<String, Object> tokenData = objectMapper.readValue(tokenBody.toString(), Map.class);
                String tokenUsername = (String) tokenData.get("username"); // Hoặc dùng usernameAttribute nếu key động
                if (tokenUsername != null) {
                    logger.info("Extracted username: {}", tokenUsername);
                    return Pair.of(tokenUsername, null);
                }
            } catch (JsonProcessingException e) {
                logger.warn("Failed to parse tokenBody as JSON: {}. Treating as plain string.", tokenBody);
                String tokenBodyStr = tokenBody.toString();
                if (!tokenBodyStr.isEmpty()) {
                    logger.info("Extracted username from plain string: {}", tokenBodyStr);
                    return Pair.of(tokenBodyStr, null);
                }
            }
        }

        logger.warn("No matching username found in tokenBody: {}", tokenBody);
        return Pair.of(null, "Not found username in token");
    }

    public Pair<Boolean, Object> isProtectedRoute(String originalUri, String originalMethod) {
        String method = originalMethod != null ? originalMethod.toUpperCase() : "";
        String uri = originalUri != null ? originalUri : "";

        if (uri.isEmpty() || method.isEmpty()) {
            logger.warn("Invalid URI '{}' or Method '{}'. Treating as unprotected.", uri, method);
            return Pair.of(false, "Invalid URI or Method");
        }

        List<RouteDto> routeList = routeService.getAllRoutes();
        if (routeList == null || routeList.isEmpty()) {
            logger.warn("No routes configured. Treating URI '{}' as unprotected.", uri);
            return Pair.of(false, "No routes configured");
        }

        // Tìm prefix dài nhất
        RouteDto bestMatch = null;
        int longestMatchLength = 0;

        for (RouteDto route : routeList) {
            String routePath = route.getRoute();
            String routeMethod = route.getMethod() != null ? route.getMethod().toUpperCase() : "";

            if (!method.equals(routeMethod)) {
                continue;
            }

            if (uri.startsWith(routePath) && routePath.length() > longestMatchLength) {
                longestMatchLength = routePath.length();
                bestMatch = route;
            }
        }

        if (bestMatch != null) {
            Boolean checkProtected = bestMatch.getCheckProtected();
            boolean isProtected = checkProtected != null && checkProtected;
            logger.info("Best match for URI '{}': route '{}' (Method: {}). Protected: {}",
                    uri, bestMatch.getRoute(), method, isProtected);
            return Pair.of(isProtected, null);
        }

        logger.info("No matching route found for URI '{}' (Method: {}). Treating as unprotected.", uri, method);
        return Pair.of(false, String.format("No matching route found for URI '%s' (Method: %s)", uri, method));
    }

}
