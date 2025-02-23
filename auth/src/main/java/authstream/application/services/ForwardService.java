package authstream.application.services;

import java.util.List;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

import authstream.domain.entities.Forward;
import authstream.infrastructure.repositories.ForwardRepository;
import jakarta.transaction.Transactional;

@Service
public class ForwardService {

    private final ForwardRepository forwardRepository;

    public ForwardService(ForwardRepository forwardRepository) {
        this.forwardRepository = forwardRepository;
    }

    @Transactional
    public void createForward(Forward forward) {
        String forwardName = forward.getName();

        String forwardAppId = forward.getApplicationId();
        String forwardMethodId = forward.getMethodId();
        String domainName = forward.getDomainName();
        String proxyHostIp = forward.getProxyHostIp();
        String callbackUrl = forward.getCallbackUrl();

        LocalDateTime forwardCreated = LocalDateTime.now(); 

        int status = forwardRepository.addForward(forwardName, forwardAppId,
         forwardMethodId, callbackUrl, forwardCreated, domainName, proxyHostIp);
        if (status == 0) {
            throw new RuntimeException("provider creation failed");
        }
    }

    @Transactional
    public void updateForward(Forward forward) {

        String forwardId = forward.getMethodId();

        String forwardName = forward.getName();
        String forwardAppId = forward.getApplicationId();
        String forwardMethodId = forward.getMethodId();
        String callbackUrl = forward.getCallbackUrl();
        String domainName = forward.getDomainName();
        String proxyHostIp = forward.getProxyHostIp();

        LocalDateTime forwardCreated = forward.getCreatedAt();

        try {
            int status = forwardRepository.updateForward(forwardId, forwardName, forwardAppId,
             forwardMethodId, forwardCreated, callbackUrl, domainName, proxyHostIp);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("error updating forward");
        }

    }

    @Transactional
    public void deleteForward(String forwardId) {
        try{
            forwardRepository.deleteForward(forwardId);
        } catch(Exception e){
            e.printStackTrace();
            throw new RuntimeException("error deleting forward");
        }
        
    }

    public List<Forward> getForwards() {
        try {
            List<Forward> forwards = forwardRepository.getAllForward();
            return forwards;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("Error getting forwards");
        }
    }

    public Forward getForwardById(String id) {
        try {
            return forwardRepository.getForwardById(id);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            throw new RuntimeException("Error getting forward by id");
        }
    }


}