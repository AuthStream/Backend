package authstream.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import authstream.application.dtos.AdminDto;
import authstream.application.services.kv.TokenEntry;
import authstream.application.services.kv.TokenStoreService;

@Service
public class PerminssionClientService {
    private final AdminService adminService;
    private static final Logger logger = LoggerFactory.getLogger(PerminssionClientService.class);
    private final PermissionProtectedService permissionProtectedService;
    private final PerminssionCheckService perminssionCheckService;

    public PerminssionClientService(AdminService adminService,
            PerminssionCheckService perminssionCheckService, PermissionProtectedService permissionProtectedService) {
        this.permissionProtectedService = permissionProtectedService;
        this.adminService = adminService;
        this.perminssionCheckService = perminssionCheckService;
    }

    public Pair<Map<String, Object>, Object> checkPermission(String originalUri, String originalMethod,
            String authHeader, String cookieHeader, Map<String, Object> requestBody) {
        try {
            Map<String, Object> processedBody = new HashMap<>(requestBody);

            Pair<Boolean, Object> isProtected = permissionProtectedService.isProtectedRoute(originalUri,
                    originalMethod);

            if (isProtected.getRight() != null) {
                // tuc la loi con me may roi. cut ra ngoai
                return Pair.of(processedBody, isProtected.getRight());

            }
            if (isProtected.getLeft() != true) {
                // tuc la route nay deo can bao ve, may co the qua binh thuong, bo deo quan tam
                return Pair.of(processedBody, null);
            }

            if (authHeader == null) {
                return Pair.of(processedBody, "Not authenticate, You don't have permission");
            }

            String tokenString;
            if (authHeader.startsWith("Bearer ")) {
                tokenString = authHeader.substring("Bearer ".length()).trim();
            } else {
                tokenString = authHeader;
            }
            TokenEntry tokenEntry = TokenStoreService.read(tokenString);
            if (tokenEntry == null || tokenEntry.isExpired()) {
                logger.warn("Invalid or expired token: {}", tokenString);
                return Pair.of(processedBody, "Invalid or expired token, Not authenticate");
            }

            // Lấy body từ token
            Object tokenBody = tokenEntry.getMessage().getBody();
            logger.info("Token validated: {}. Body: {}", tokenString, tokenBody);
            List<AdminDto> admins = adminService.getAllAdmins();
            Pair<String, Object> userNameChecker = permissionProtectedService.extractUsernameFromTokenBody(tokenBody,
                    admins);

            if (userNameChecker.getRight() != null) {
                return Pair.of(processedBody, "Something wrong with token data" + userNameChecker.getRight());
            }

            String username = userNameChecker.getLeft();
            Pair<Boolean, Object> checkPerminssionPair = perminssionCheckService.checkPermission(username, originalUri,
                    originalMethod);
            if (checkPerminssionPair.getRight() != null) {
                return Pair.of(processedBody, checkPerminssionPair.getRight());
            }
            processedBody.put("authData", tokenBody);

            return Pair.of(processedBody, null);

        } catch (Exception e) {
            logger.error("Error checking permission for URI {}: {}", originalUri, e.getMessage());
            return Pair.of(null, "something wrong with server: " + e.getMessage());
        }
    }
}
