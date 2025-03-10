// package authstream.application.services;

// import java.util.List;
// import java.time.LocalDateTime;

// import org.springframework.stereotype.Service;

// import authstream.domain.entities.Application;
// import authstream.infrastructure.repositories.ApplicationRepository;
// import jakarta.transaction.Transactional;

// @Service
// public class ApplicationService {

//     private final ApplicationRepository applicationRepository;
//     private final ProviderService providerService;

//     public ApplicationService(ApplicationRepository applicationRepository, ProviderService providerService) {
//         this.applicationRepository = applicationRepository;
//         this.providerService = providerService;
//     }

//     @Transactional
//     public void createApplication(Application application) {
//         String applicationName = application.getName();
//         String applicationAdminId = application.getAdminId();
//         LocalDateTime applicationCreated = LocalDateTime.now(); 
//         LocalDateTime applicationUpdated = LocalDateTime.now();

//         int status = applicationRepository.addApplication(applicationName, null,
//          applicationAdminId, applicationCreated, applicationUpdated);
//         if (status == 0) {
//             throw new RuntimeException("Application creation failed");
//         }
//     }

//     @Transactional
//     public void updateApplication(Application application) {

//         String applicationId = application.getId();

//         String applicationName = application.getName();
//         String applicationAdminId = application.getAdminId();
//         LocalDateTime applicationUpdated = LocalDateTime.now();
//         String providerId = application.getProvider().getId();

//         try {
//             int status = applicationRepository.updateApplication(applicationId, applicationName, providerId, applicationAdminId, applicationUpdated);
//         } catch(Exception e) {
//             e.printStackTrace();
//         }

//     }

//     @Transactional
//     public void deleteApplication(String applicationId) {
//         try {
//             Application deleteApplication = applicationRepository.getAppById(applicationId);
//             if (deleteApplication == null) {
//                 System.err.println("Uknown delete to delete");
//                 throw new RuntimeException("Application not found");
//             }
//             providerService.deleteProvider(deleteApplication.getProvider().getId());
//             applicationRepository.deleteApplication(applicationId);
//         } catch (Exception e) {
//             // TODO: handle exception
//             e.printStackTrace();
//             throw new RuntimeException("error deleting application");
//         }

//     }

//     public List<Application> getApplications() {
//         try {
//             List<Application> applications = applicationRepository.getAllApplications();
//             return applications;
//         } catch (Exception e) {
//             // TODO: handle exception
//             e.printStackTrace();
//             throw new RuntimeException("error getting applications");
//         }

//     }

//     public Application getApplicationById(String id) {
//         try {
//             return applicationRepository.getAppById(id);
//         } catch (Exception e) {
//             // TODO: handle exception
//             e.printStackTrace();
//             throw new RuntimeException("error getting application by id");
//         }

//     }

// }
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
        application.setId(UUID.randomUUID());
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

        UUID providerId = application.getProvider() != null ? application.getProvider().getId() : dto.providerId;

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
    public void deleteApplication(UUID applicationId) {
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