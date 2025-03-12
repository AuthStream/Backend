package authstream.application.services;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import authstream.application.dtos.ForwardDto;
import authstream.application.dtos.ProviderDto;
import authstream.application.mappers.ProviderMapper;
import authstream.domain.entities.Provider;
import authstream.domain.entities.ProviderType;
import authstream.infrastructure.repositories.ProviderRepository;
import jakarta.transaction.Transactional;

@Service
public class ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;
    private final ForwardService forwardService;

    public ProviderService(ProviderRepository providerRepository, ForwardService forwardService) {
        this.providerRepository = providerRepository;
        this.forwardService = forwardService;
    }

    @Transactional
    public ProviderDto createProvider(ProviderDto dto) {
        if (dto.type == null) {
            throw new IllegalArgumentException("Provider type is required");
        }
        if (dto.name == null) {
            throw new IllegalArgumentException("Provider name is required");
        }

        Provider provider = ProviderMapper.toEntity(dto);
        provider.setId(UUID.randomUUID());
        ForwardDto createdForward = null;

        if (dto.type == ProviderType.FORWARD) {
            if (dto.proxyHostIp == null || dto.domainName == null || dto.callbackUrl == null) {
                throw new IllegalArgumentException(
                        "proxyHostIp, domainName, and callbackUrl are required for FORWARD type");
            }
            ForwardDto forwardDto = new ForwardDto();

            forwardDto.methodId = UUID.randomUUID();
            forwardDto.applicationId = dto.applicationId;
            forwardDto.name = dto.methodName != null ? dto.methodName : dto.name;
            forwardDto.proxyHostIp = dto.proxyHostIp;
            forwardDto.domainName = dto.domainName;
            forwardDto.callbackUrl = dto.callbackUrl;

            createdForward = forwardService.createForward(forwardDto);
            provider.setMethodId(createdForward.methodId);
            provider.setApplicationId(createdForward.applicationId);
        } else {
            if (dto.methodId == null) {
                throw new IllegalArgumentException("methodId is required for non-FORWARD type");
            }
            provider.setMethodId(dto.methodId);
            provider.setApplicationId(dto.applicationId);
        }

        int status = providerRepository.addProvider(
                provider.getId(),
                provider.getName(),
                provider.getApplicationId(),
                provider.getMethodId(),
                provider.getType().name(),
                provider.getCreatedAt(),
                provider.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Provider creation failed");
        }

        ProviderDto result = ProviderMapper.toDto(provider);
        if (dto.type == ProviderType.FORWARD && createdForward != null) {
            result.methodName = createdForward.name; // Sửa lại để lấy name từ ForwardDto
            result.proxyHostIp = createdForward.proxyHostIp;
            result.domainName = createdForward.domainName;
            result.callbackUrl = createdForward.callbackUrl;
        }

        System.out.println("log data provider:: >> " + result);
        return result;
        // return ProviderMapper.toDto(provider);

    }

    @Transactional
    public ProviderDto updateProvider(ProviderDto dto) {
        if (dto.id == null) {
            throw new RuntimeException("Provider ID is required for update");
        }
        if (dto.type == null) {
            throw new IllegalArgumentException("Provider type is required");
        }
        Provider provider = ProviderMapper.toEntity(dto);
        provider.setId(dto.id);
        provider.setUpdatedAt(LocalDateTime.now());

        try {
            int status = providerRepository.updateProvider(
                    provider.getId(),
                    provider.getName(),
                    provider.getApplicationId(),
                    provider.getMethodId(),
                    provider.getUpdatedAt(),
                    provider.getType().name());
            if (status == 0) {
                throw new RuntimeException("Provider update failed");
            }
            Provider updatedProvider = providerRepository.getProviderById(provider.getId());
            return ProviderMapper.toDto(updatedProvider);
        } catch (Exception e) {
            logger.error("Error updating provider", e);
            throw new RuntimeException("Error updating provider", e);
        }
    }

    @Transactional
    public void deleteProvider(UUID providerId) {
        try {
            Provider provider = providerRepository.getProviderById(providerId);
            if (provider == null) {
                throw new RuntimeException("Provider not found");
            }

            providerRepository.deleteProvider(providerId);
        } catch (Exception e) {
            logger.error("Error deleting provider", e);
            throw new RuntimeException("Error deleting provider", e);
        }
    }

    public List<ProviderDto> getProviders() {
        try {
            List<Provider> providers = providerRepository.getAllProviders();
            logger.debug("Retrieved providers: {}", providers);
            return providers.stream()
                    .map(ProviderMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting providers", e);
            throw new RuntimeException("Error getting providers", e);
        }
    }

    public ProviderDto getProviderById(UUID id) {
        try {
            Provider provider = providerRepository.getProviderById(id);
            logger.debug("Retrieved provider by id {}: {}", id, provider);
            return ProviderMapper.toDto(provider);
        } catch (Exception e) {
            logger.error("Error getting provider by id {}", id, e);
            throw new RuntimeException("Error getting provider by id", e);
        }
    }
}