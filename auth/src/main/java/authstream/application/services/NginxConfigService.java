package authstream.application.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import authstream.application.dtos.ForwardDto;

@Service
public class NginxConfigService {

    private static final Logger logger = LoggerFactory.getLogger(NginxConfigService.class);
    private final ForwardService forwardService;
    private final NginxConfigGeneratorService configGeneratorService;
    private static final int DEFAULT_NGINX_PORT = 8080;

    @Autowired
    public NginxConfigService(
            ForwardService forwardService,
            ApplicationService applicationService,
            NginxConfigGeneratorService configGeneratorService) {
        this.forwardService = forwardService;
        this.configGeneratorService = configGeneratorService;
    }

    public Map<String, String> generateConfigs(UUID applicationId) throws IOException {
        ForwardDto forwardDto = null;

        if (applicationId != null) {
            forwardDto = forwardService.getForwardByApplicationId(applicationId);
        }
        if (forwardDto == null) {
            logger.error("No Forward found for applicationId: {} or providerId: {}", applicationId);
            throw new IllegalArgumentException("No Forward configuration found for the given IDs");
        }

        logger.info("Found ForwardDto: {}", forwardDto);

        // Lấy thông tin từ ForwardDto
        String authServer = "http://" + forwardDto.getProxyHostIp() + ":" +
                DEFAULT_NGINX_PORT;
        String appServerDomain = forwardDto.getDomainName();
        String domainName = forwardDto.getDomainName();

        // Gọi atomic service để sinh config
        String nginxConfig = configGeneratorService.generateNginxConfig(DEFAULT_NGINX_PORT, domainName);
        String jsConfig = configGeneratorService.generateJsConfig(authServer, appServerDomain);

        return Map.of(
                "nginx.conf", nginxConfig,
                "authstream.js", jsConfig);
    }
}