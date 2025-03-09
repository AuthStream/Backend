package authstream.application.services;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import authstream.application.dtos.ApplicationDto;
import authstream.application.mappers.ApplicationMapper;
import authstream.domain.entities.Application;
import authstream.infrastructure.repositories.ApplicationRepository;
import jakarta.transaction.Transactional;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ProviderService providerService;

    public ApplicationService(ApplicationRepository applicationRepository, ProviderService providerService) {
        this.applicationRepository = applicationRepository;
        this.providerService = providerService;
    }

    @Transactional
    public ApplicationDto createApplication(ApplicationDto dto) {
        Application application = ApplicationMapper.toEntity(dto);
        // Server sinh id, createdAt, updatedAt
        application.setId(UUID.randomUUID().toString());
        application.setCreatedAt(LocalDateTime.now());
        application.setUpdatedAt(LocalDateTime.now());

        int status = applicationRepository.addApplication(
                application.getId(),
                application.getName(),
                application.getProvider() != null ? application.getProvider().getId() : null,
                application.getAdminId(),
                application.getCreatedAt(),
                application.getUpdatedAt());
        if (status == 0) {
            throw new RuntimeException("Application creation failed");
        }
        return ApplicationMapper.toDto(application);
    }

    @Transactional
    public void updateApplication(ApplicationDto dto) {
        if (dto.id == null) {
            throw new RuntimeException("Application ID is required for update");
        }
        Application application = ApplicationMapper.toEntity(dto);
        application.setId(dto.id); // Dùng id từ DTO để xác định record
        application.setUpdatedAt(LocalDateTime.now());

        String providerId = application.getProvider() != null ? application.getProvider().getId() : dto.providerId;

        try {
            int status = applicationRepository.updateApplication(
                    application.getId(),
                    application.getName(),
                    providerId,
                    application.getAdminId(),
                    application.getUpdatedAt());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating application");
        }
    }

    @Transactional
    public void deleteApplication(String applicationId) {
        try {
            Application application = applicationRepository.getAppById(applicationId);
            if (application == null) {
                throw new RuntimeException("Application not found");
            }
            providerService.deleteProvider(application.getProvider().getId());
            applicationRepository.deleteApplication(applicationId);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting application");
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

    public ApplicationDto getApplicationById(String id) {
        try {
            Application application = applicationRepository.getAppById(id);
            return ApplicationMapper.toDto(application);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error getting application by id");
        }
    }
}