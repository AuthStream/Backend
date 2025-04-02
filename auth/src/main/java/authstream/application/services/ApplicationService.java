package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import authstream.application.dtos.ApplicationDto;
import authstream.application.mappers.ApplicationMapper;
import authstream.domain.entities.Application;
import authstream.domain.entities.Forward;
import authstream.domain.entities.Token;
import authstream.infrastructure.repositories.ApplicationRepository;
import authstream.infrastructure.repositories.ForwardRepository;
import authstream.infrastructure.repositories.TokenRepository;
import jakarta.transaction.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProviderService providerService;
    private final TokenRepository tokenRepository;
    private final ForwardRepository forwardRepository;

    public ApplicationService(ApplicationRepository applicationRepository, ProviderService providerService,
            TokenRepository tokenRepository, ForwardRepository forwardRepository) {
        this.applicationRepository = applicationRepository;
        this.tokenRepository = tokenRepository;
        this.providerService = providerService;
        this.forwardRepository = forwardRepository;
    }

    @Transactional
    public ApplicationDto createApplication(ApplicationDto dto) {
        if (dto.name == null) {
            throw new IllegalArgumentException("Application name is required");
        }
        if (dto.adminId == null) {
            throw new IllegalArgumentException("Admin ID is required");
        }
        if (dto.tokenId == null) {
            throw new IllegalArgumentException("Token ID is required");
        }

        Token token = tokenRepository.findById(dto.tokenId)
                .orElseThrow(() -> new IllegalArgumentException("Token with ID " + dto.tokenId + " does not exist"));

        Application application = ApplicationMapper.toEntity(dto);
        application.setId(UUID.randomUUID());
        application.setToken(token);

        int status = applicationRepository.addApplication(
                application.getId(),
                application.getName(),
                application.getProvider() != null ? application.getProvider().getId() : null,
                application.getAdminId(),
                application.getToken().getId(),
                application.getCreatedAt(),
                application.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Application creation failed");
        }

        return ApplicationMapper.toDto(application);
    }

    @Transactional
    public void updateApplication(ApplicationDto dto) {
        // Validate DTO
        if (dto.id == null) {
            throw new IllegalArgumentException("Application ID is required for update");
        }
        if (dto.name == null || dto.name.trim().isEmpty()) {
            throw new IllegalArgumentException("Application name is required");
        }
        if (dto.adminId == null) {
            throw new IllegalArgumentException("Admin ID is required");
        }
        if (dto.tokenId == null) {
            throw new IllegalArgumentException("Token ID is required");
        }

        // Check if application exists
        if (!applicationRepository.existsById(dto.id)) {
            throw new RuntimeException("Application with ID " + dto.id + " not found");
        }

        // Map DTO to entity
        Application application = ApplicationMapper.toEntity(dto);
        application.setId(dto.id);
        application.setUpdatedAt(LocalDateTime.now());

        // Xử lý providerId và tokenId
        UUID providerId = (application.getProvider() != null && application.getProvider().getId() != null)
                ? application.getProvider().getId()
                : dto.providerId;
        UUID tokenId = (application.getToken() != null && application.getToken().getId() != null)
                ? application.getToken().getId()
                : dto.tokenId;

        try {
            int updatedRows = applicationRepository.updateApplication(
                    application.getId(),
                    application.getName(),
                    providerId,
                    application.getAdminId(),
                    tokenId,
                    application.getUpdatedAt());
            if (updatedRows == 0) {
                throw new RuntimeException("Failed to update application: No rows affected");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating application: " + e.getMessage(), e);
        }
    }

    @Transactional
    public void deleteApplication(UUID applicationId) {
        try {
            Application application = applicationRepository.getAppById(applicationId);
            tokenRepository.updateApplicationIdToNull(applicationId);
            if (application == null) {
                throw new RuntimeException("Application not found");
            }
            Forward forwardApplication = forwardRepository.getFowForwardByApplication(applicationId);
            if (application.getProvider() != null) {

                Pair<Boolean, Object> resultDeleteProvider = providerService
                        .deleteProvider(application.getProvider().getId());
            }
            if (forwardApplication != null) {

                Pair<Boolean, Object> resultDeleteProvider = providerService
                        .deleteProviderByMethod(forwardApplication.getMethodId());

            }

            forwardRepository.deleteForwardByApplicationId(applicationId);

            applicationRepository.deleteApplication(applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting application: " + e.getMessage());
        }
    }

    public List<ApplicationDto> getApplications() {
        try {
            List<Application> applications = applicationRepository.getAllApplications();
            return applications.stream()
                    .map(ApplicationMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting applications");
        }
    }

    public ApplicationDto getApplicationById(UUID id) {
        try {
            Application application = applicationRepository.getAppById(id);
            return ApplicationMapper.toDto(application);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting application by id");
        }
    }
}