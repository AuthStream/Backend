package authstream.application.services;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

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
    public void createApplication(Application application) {
        String applicationName = application.getName();
        String applicationAdminId = application.getAdminId();
        LocalDateTime applicationCreated = LocalDateTime.now(); 
        LocalDateTime applicationUpdated = LocalDateTime.now();

        int status = applicationRepository.addApplication(applicationName, null,
         applicationAdminId, applicationCreated, applicationUpdated);
        if (status == 0) {
            throw new RuntimeException("Application creation failed");
        }
    }

    @Transactional
    public void updateApplication(Application application) {

        String applicationId = application.getId();

        String applicationName = application.getName();
        String applicationAdminId = application.getAdminId();
        LocalDateTime applicationUpdated = LocalDateTime.now();
        String providerId = application.getProvider().getId();

        try {
            int status = applicationRepository.updateApplication(applicationId, applicationName, providerId, applicationAdminId, applicationUpdated);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    public void deleteApplication(String applicationId) {
        try {
            Application deleteApplication = applicationRepository.getAppById(applicationId);
            if (deleteApplication == null) {
                System.err.println("Uknown delete to delete");
                throw new RuntimeException("Application not found");
            }
            providerService.deleteProvider(deleteApplication.getProvider().getId());
            applicationRepository.deleteApplication(applicationId);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("error deleting application");
        }
        
    }

    public List<Application> getApplications() {
        try {
            List<Application> applications = applicationRepository.getAllApplications();
            return applications;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("error getting applications");
        }
        
    }

    public Application getApplicationById(String id) {
        try {
            return applicationRepository.getAppById(id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("error getting application by id");
        }
        
    }


}