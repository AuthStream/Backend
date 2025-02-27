// package authstream.application.services;

// import java.util.List;

// import java.time.LocalDateTime;
// import org.springframework.stereotype.Service;

// import authstream.domain.entities.Forward;
// import authstream.infrastructure.repositories.ForwardRepository;
// import jakarta.transaction.Transactional;

// @Service
// public class ForwardService {

//     private final ForwardRepository forwardRepository;

//     public ForwardService(ForwardRepository forwardRepository) {
//         this.forwardRepository = forwardRepository;
//     }

//     @Transactional
//     public void createForward(Forward forward) {
//         String forwardName = forward.getName();

//         String forwardAppId = forward.getApplicationId();
//         String forwardMethodId = forward.getMethodId();
//         String domainName = forward.getDomainName();
//         String proxyHostIp = forward.getProxyHostIp();
//         String callbackUrl = forward.getCallbackUrl();

//         LocalDateTime forwardCreated = LocalDateTime.now(); 

//         int status = forwardRepository.addForward(forwardName, forwardAppId,
//          forwardMethodId, callbackUrl, forwardCreated, domainName, proxyHostIp);
//         if (status == 0) {
//             throw new RuntimeException("provider creation failed");
//         }
//     }

//     @Transactional
//     public void updateForward(Forward forward) {

//         String forwardId = forward.getMethodId();

//         String forwardName = forward.getName();
//         String forwardAppId = forward.getApplicationId();
//         String forwardMethodId = forward.getMethodId();
//         String callbackUrl = forward.getCallbackUrl();
//         String domainName = forward.getDomainName();
//         String proxyHostIp = forward.getProxyHostIp();

//         LocalDateTime forwardCreated = forward.getCreatedAt();

//         try {
//             int status = forwardRepository.updateForward(forwardId, forwardName, forwardAppId,
//              forwardMethodId, forwardCreated, callbackUrl, domainName, proxyHostIp);
//         } catch(Exception e) {
//             e.printStackTrace();
//             throw new RuntimeException("error updating forward");
//         }

//     }

//     @Transactional
//     public void deleteForward(String forwardId) {
//         try{
//             forwardRepository.deleteForward(forwardId);
//         } catch(Exception e){
//             e.printStackTrace();
//             throw new RuntimeException("error deleting forward");
//         }

//     }

//     public List<Forward> getForwards() {
//         try {
//             List<Forward> forwards = forwardRepository.getAllForward();
//             return forwards;
//         } catch (Exception e) {
//             // TODO: handle exception
//             e.printStackTrace();
//             throw new RuntimeException("Error getting forwards");
//         }
//     }

//     public Forward getForwardById(String id) {
//         try {
//             return forwardRepository.getForwardById(id);
//         } catch (Exception e) {
//             // TODO: handle exception
//             e.printStackTrace();
//             throw new RuntimeException("Error getting forward by id");
//         }
//     }

// }

package authstream.application.services;

import java.util.List;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import authstream.application.dtos.ForwardDto;
import authstream.application.mappers.ForwardMapper;
import authstream.domain.entities.Forward;
import authstream.infrastructure.repositories.ForwardRepository;
import jakarta.transaction.Transactional;

@Service
public class ForwardService {

    private static final Logger logger = LoggerFactory.getLogger(ForwardService.class);

    private final ForwardRepository forwardRepository;

    public ForwardService(ForwardRepository forwardRepository) {
        this.forwardRepository = forwardRepository;
    }

    @Transactional
    public ForwardDto createForward(ForwardDto dto) {
        if (dto.name == null || dto.proxyHostIp == null ||
                dto.domainName == null || dto.callbackUrl == null) {
            throw new IllegalArgumentException(
                    "All required fields (applicationId, name, proxyHostIp, domainName, callbackUrl) must be provided");
        }
        Forward forward = ForwardMapper.toEntity(dto);
        String methodId = UUID.randomUUID().toString();
        forward.setMethodId(methodId);
        forward.setCreatedAt(LocalDateTime.now());

        int status = forwardRepository.addForward(
                forward.getName(),
                forward.getApplicationId(),
                forward.getMethodId(),
                forward.getCallbackUrl(),
                forward.getCreatedAt(),
                forward.getDomainName(),
                forward.getProxyHostIp());
        if (status == 0) {
            throw new RuntimeException("Forward creation failed");
        }
        return ForwardMapper.toDto(forward);
    }

    @Transactional
    public ForwardDto updateForward(ForwardDto dto) {
        if (dto.method_id == null) {
            throw new IllegalArgumentException("Forward methodId is required for update");
        }
        Forward forward = ForwardMapper.toEntity(dto);
        forward.setMethodId(dto.method_id);
        forward.setCreatedAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now()); // Giữ createdAt cũ nếu không
                                                                                           // cung cấp

        try {
            int status = forwardRepository.updateForward(
                    forward.getMethodId(),
                    forward.getName(),
                    forward.getApplicationId(),
                    forward.getMethodId(),
                    forward.getCreatedAt(),
                    forward.getCallbackUrl(),
                    forward.getDomainName(),
                    forward.getProxyHostIp());
            if (status == 0) {
                throw new RuntimeException("Forward update failed");
            }
            Forward updatedForward = forwardRepository.getForwardById(forward.getMethodId());
            return ForwardMapper.toDto(updatedForward);
        } catch (Exception e) {
            logger.error("Error updating forward", e);
            throw new RuntimeException("Error updating forward", e);
        }
    }

    @Transactional
    public void deleteForward(String forwardId) {
        try {
            forwardRepository.deleteForward(forwardId);
        } catch (Exception e) {
            logger.error("Error deleting forward", e);
            throw new RuntimeException("Error deleting forward", e);
        }
    }

    public List<ForwardDto> getForwards() {
        try {
            List<Forward> forwards = forwardRepository.getAllForward();
            return forwards.stream()
                    .map(ForwardMapper::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error getting forwards", e);
            throw new RuntimeException("Error getting forwards", e);
        }
    }

    public ForwardDto getForwardById(String id) {
        try {
            Forward forward = forwardRepository.getForwardById(id);
            return ForwardMapper.toDto(forward);
        } catch (Exception e) {
            logger.error("Error getting forward by id {}", id, e);
            throw new RuntimeException("Error getting forward by id", e);
        }
    }
}