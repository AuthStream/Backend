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
import authstream.application.mappers.ForwardMapper;
import authstream.application.mappers.ProviderMapper;
import authstream.domain.entities.Forward;
import authstream.domain.entities.Provider;
import authstream.domain.entities.ProviderType;
import authstream.infrastructure.repositories.ProviderRepository;
import authstream.infrastructure.repositories.ForwardRepository;
import jakarta.transaction.Transactional;

@Service
public class ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;
    private final ForwardRepository forwardRepository;

    public ProviderService(ProviderRepository providerRepository, ForwardRepository forwardRepository) {
        this.providerRepository = providerRepository;
        this.forwardRepository = forwardRepository;
    }

    @Transactional
    public ProviderDto createProvider(ProviderDto dto) {
        if (dto.type == null) {
            throw new IllegalArgumentException("Provider type is required");
        }
        Provider provider = ProviderMapper.toEntity(dto);
        UUID providerId = UUID.randomUUID();
        provider.setId(providerId);
        provider.setCreatedAt(LocalDateTime.now());
        provider.setUpdatedAt(LocalDateTime.now());

        if (dto.type == ProviderType.FORWARD && dto.methodId != null) {
            Forward forward = forwardRepository.findById(dto.methodId).orElse(null);

            if (forward != null) {
                ForwardDto forwardDto = ForwardMapper.toDto(forward);
                forwardDto.setName(dto.name != null ? dto.name : forward.getName());

            } else {
                ForwardDto forwardDto = new ForwardDto();
            }

            provider.setMethodId(forward.getMethodId());
            provider.setApplicationId(forward.getApplicationId());
        } else {
            logger.warn("Invalid Forward data: methodId is null or type is not FORWARD");
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
        return ProviderMapper.toDto(provider);
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