package authstream.application.services;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import authstream.domain.entities.Provider;
import authstream.domain.entities.ProviderType;
import authstream.infrastructure.repositories.ForwardRepository;
import authstream.infrastructure.repositories.ProviderRepository;
import jakarta.transaction.Transactional;

@Service
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ForwardRepository forwardRepository;


    public ProviderService(ProviderRepository providerRepository, 
                             ForwardRepository forwardRepository) {


        this.providerRepository = providerRepository;
        this.forwardRepository = forwardRepository;
    }

    @Transactional
    public void createProvider(Provider provider) {
        String providerName = provider.getName();
        ProviderType providerType = provider.getType();
        String providerAppId = provider.getApplicationId();
        String providerMethodId = provider.getMethodId();
        LocalDateTime providerCreated = LocalDateTime.now(); 
        LocalDateTime providerUpdated = LocalDateTime.now();

        int status = providerRepository.addProvider(providerName, providerAppId,
         providerMethodId, providerType,  providerCreated, providerUpdated);
        if (status == 0) {
            throw new RuntimeException("provider creation failed");
        }
    }

    @Transactional
    public void updateProvider(Provider provider) {
        // TODO: create new method then delete old method

        String providerId = provider.getId();

        String providerName = provider.getName();
        String providerAppId = provider.getApplicationId();
        String providerMethodId = provider.getMethodId();
        ProviderType providerType = provider.getType();
        LocalDateTime providerUpdated = LocalDateTime.now();

        try {
            int status = providerRepository.updateProvider(providerId, providerName, providerAppId,
             providerMethodId, providerUpdated, providerType);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Transactional
    public void deleteProvider(String providerId) {
        // TODO: Delete provider then method object
        Provider deleteProvider = providerRepository.getProviderById(providerId);
        if(deleteProvider == null) {
            System.err.println("Uknown provider to delete");
            return;
        }
        String delete_type = deleteProvider.getType().toString();
        if("FORWARD".equals(delete_type)){
            forwardRepository.deleteForward(deleteProvider.getMethodId());
        }
        providerRepository.deleteProvider(providerId);

    }

    public List<Provider> getProviders() {
        try {
            List<Provider> providers = providerRepository.getAllProviders();
            return providers;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("error getting providers");
        }
        
    }

    public Provider getProviderById(String id) {
        try {
            return providerRepository.getProviderById(id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("error getting provider by id");
        }
        
    }


}