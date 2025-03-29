package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import authstream.application.dtos.ForwardDto;
import authstream.application.dtos.ProviderDto;
import authstream.application.mappers.ProviderMapper;
import authstream.domain.entities.Forward;
import authstream.domain.entities.Provider;
import authstream.domain.entities.ProviderType;
import authstream.infrastructure.repositories.ApplicationRepository;
import authstream.infrastructure.repositories.ForwardRepository;
import authstream.infrastructure.repositories.ProviderRepository;
import jakarta.transaction.Transactional;

@Service
public class ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;
    private final ForwardService forwardService;
    private final ForwardRepository forwardRepository;
    private final ApplicationRepository applicationRepository;

    public ProviderService(ProviderRepository providerRepository, ForwardService forwardService,
            ForwardRepository forwardRepository, ApplicationRepository applicationRepository) {
        this.providerRepository = providerRepository;
        this.forwardService = forwardService;
        this.forwardRepository = forwardRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public Pair<ProviderDto, Object> createProvider(ProviderDto dto) {
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
                // throw new IllegalArgumentException(
                // "proxyHostIp, domainName, and callbackUrl are required for FORWARD type");

                return Pair.of(null, "proxyHostIp, domainName, and callbackUrl are required for FORWARD type");
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
                // throw new IllegalArgumentException("methodId is required for non-FORWARD
                // type");
                return Pair.of(null, "methodId is required for non-FORWARD type");
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
            // throw new RuntimeException("Provider creation failed");
            return Pair.of(null, "Provider creation failed");
        }

        ProviderDto result = ProviderMapper.toDto(provider);
        if (dto.type == ProviderType.FORWARD && createdForward != null) {
            result.methodName = createdForward.name;
            result.proxyHostIp = createdForward.proxyHostIp;
            result.domainName = createdForward.domainName;
            result.callbackUrl = createdForward.callbackUrl;
        }

        System.out.println("log data provider:: >> " + result);
        return Pair.of(result, null);

    }

    @Transactional
    public Pair<ProviderDto, Object> updateProvider(ProviderDto dto) {
        if (dto.id == null) {
            return Pair.of(null, "Provider ID is required for update");
        }
        if (dto.type == null) {
            return Pair.of(null, "Provider type is required");

        }

        Provider existingProvider = providerRepository.getProviderById(dto.id);
        if (existingProvider == null) {
            return Pair.of(null, "Provider with ID \" + dto.id + \" not found");

        }
        // logger.debug("Existing provider: {}", existingProvider);

        Provider provider = ProviderMapper.toEntity(dto);
        provider.setId(dto.id);
        provider.setMethodId(existingProvider.getMethodId());
        provider.setUpdatedAt(LocalDateTime.now());
        // logger.debug("Updated provider: {}", provider);

        if (dto.type == ProviderType.FORWARD &&
                (dto.methodName != null || dto.proxyHostIp != null || dto.domainName != null
                        || dto.callbackUrl != null)) {
            if (provider.getMethodId() == null) {
                // throw new RuntimeException("No Forward linked to this Provider (methodId is
                // null)");
                return Pair.of(null, "PNo Forward linked to this Provider (methodId is null)");

            }

            ForwardDto forwardDto = new ForwardDto();
            forwardDto.methodId = provider.getMethodId();
            forwardDto.applicationId = provider.getApplicationId();

            Forward existingForward = forwardRepository.getForwardById(provider.getMethodId());
            if (existingForward == null) {
                // throw new RuntimeException("Forward with methodId " + provider.getMethodId()
                // + " not found");
                return Pair.of(null, "Forward with methodId " + provider.getMethodId() + " not found.");
            }
            // logger.debug("Existing forward: {}", existingForward);

            forwardDto.name = dto.methodName != null ? dto.methodName : existingForward.getName();
            forwardDto.proxyHostIp = dto.proxyHostIp != null ? dto.proxyHostIp : existingForward.getProxyHostIp();
            forwardDto.domainName = dto.domainName != null ? dto.domainName : existingForward.getDomainName();
            forwardDto.callbackUrl = dto.callbackUrl != null ? dto.callbackUrl : existingForward.getCallbackUrl();

            forwardService.updateForward(forwardDto);
            // logger.debug("Updated forward: {}", forwardDto);
        }

        try {
            int status = providerRepository.updateProvider(
                    provider.getId(),
                    provider.getName(),
                    provider.getApplicationId(),
                    provider.getMethodId(),
                    provider.getUpdatedAt(),
                    provider.getType().name());
            if (status == 0) {
                return Pair.of(null, "Provider update failed");
            }

            Provider updatedProvider = providerRepository.getProviderById(provider.getId());
            ProviderDto result = ProviderMapper.toDto(updatedProvider);
            result.id = provider.getId();
            if (dto.type == ProviderType.FORWARD && updatedProvider.getMethodId() != null) {
                Forward forward = forwardRepository.getForwardById(updatedProvider.getMethodId());

                if (forward != null) {

                    result.methodName = forward.getName();
                    result.proxyHostIp = forward.getProxyHostIp();
                    result.domainName = forward.getDomainName();
                    result.callbackUrl = forward.getCallbackUrl();
                }
            }
            // logger.debug("Final result: {}", result);
            return Pair.of(result, null);
        } catch (Exception e) {
            // logger.error("Error updating provider", e);
            return Pair.of(null, "Error updating provider: " + e.getMessage());
        }
    }

    @Transactional
    public Pair<Boolean, Object> deleteProvider(UUID providerId) {
        try {
            Provider provider = providerRepository.getProviderById(providerId);
            if (provider == null) {
                return Pair.of(null, "provider with " + providerId + " not found");
            }
            applicationRepository.updateProviderIdToNull(providerId);
            providerRepository.deleteProvider(providerId);
            return Pair.of(true, null);

        } catch (Exception e) {
            // logger.error("Error deleting provider", e);
            // throw new RuntimeException("Error deleting provider" + e.getMessage());

            return Pair.of(null, "Error deleting provider: " + e.getMessage());
        }
    }

    @Transactional
    public Pair<Boolean, Object> deleteProviderByMethod(UUID providerId) {
        try {
            Provider provider = providerRepository.getProviderById(providerId);

            if (provider == null) {
                return Pair.of(null, "provider not found");
            }
            applicationRepository.updateProviderIdToNull(providerId);
            providerRepository.deleteProviderByMethodId(providerId);
            return Pair.of(true, null);
        } catch (Exception e) {
            // logger.error("Error deleting provider", e);
            // throw new RuntimeException("Error deleting provider" + e.getMessage());
            return Pair.of(null, "Error deleting provider: " + e.getMessage());
        }
    }

    public Pair<List<ProviderDto>, Object> getProviders() {
        try {
            List<Provider> providers = providerRepository.getAllProviders();
            logger.debug("Retrieved providers: {}", providers);

            List<ProviderDto> resultList = providers.stream()
                    .map(provider -> {
                        ProviderDto dto = ProviderMapper.toDto(provider);
                        if (provider.getType() == ProviderType.FORWARD && provider.getMethodId() != null) {
                            Forward forward = forwardRepository.getForwardById(provider.getMethodId());
                            if (forward != null) {
                                logger.debug("Forward data for provider {}: {}", provider.getId(), forward);
                                dto.methodName = forward.getName();
                                dto.proxyHostIp = forward.getProxyHostIp();
                                dto.domainName = forward.getDomainName();
                                dto.callbackUrl = forward.getCallbackUrl();
                            } else {
                                logger.warn("No Forward found for methodId: {}", provider.getMethodId());
                            }
                        }
                        return dto;
                    })
                    .collect(Collectors.toList());
            return Pair.of(resultList, null);
        } catch (Exception e) {
            logger.error("Error getting providers", e);
            // throw new RuntimeException("Error getting providers", e);
            return Pair.of(null, "Error getting providers: " + e.getMessage());

        }
    }

    public Pair<ProviderDto, Object> getProviderById(UUID id) {
        try {
            Provider provider = providerRepository.getProviderById(id);
            if (provider == null) {
                return Pair.of(null, "Provider not found");
            }

            UUID methodId = provider.getMethodId();
            // logger.debug("method id data {} : {}", methodId);
            ProviderDto result = ProviderMapper.toDto(provider);

            if (provider.getType().toString() == "FORWARD" && provider.getMethodId() != null) {
                Forward dataForwardDto = forwardRepository.getForwardById(methodId);
                logger.debug("test data {}", dataForwardDto);
                result.methodName = dataForwardDto.getName();
                result.proxyHostIp = dataForwardDto.getProxyHostIp();
                result.domainName = dataForwardDto.getDomainName();
                result.callbackUrl = dataForwardDto.getCallbackUrl();
            }
            // logger.debug("Retrieved provider by id {}: {}", id, provider);
            return Pair.of(result, null);
        } catch (Exception e) {
            // logger.error("Error getting provider by id {}", id, e);
            // throw new RuntimeException("Error getting provider by id", e);
            return Pair.of(null, "Error getting provider by id: " + e.getMessage());
        }
    }
}