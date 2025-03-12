
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
        if (dto.name == null) {
            throw new IllegalArgumentException("Forward name is required");
        }
        if (dto.proxyHostIp == null) {
            throw new IllegalArgumentException("Proxy host IP is required");
        }
        if (dto.domainName == null) {                                          
            throw new IllegalArgumentException("Domain name is required");
        }
        if (dto.callbackUrl == null) {
            throw new IllegalArgumentException("Callback URL is required");
        }

        Forward forward = ForwardMapper.toEntity(dto);
        forward.setMethodId(UUID.randomUUID());

        int status = forwardRepository.addForward(
                forward.getMethodId(),
                forward.getApplicationId(),
                forward.getName(),
                forward.getProxyHostIp(),
                forward.getCreatedAt(),
                forward.getCallbackUrl(),
                forward.getDomainName()
        );
        if (status == 0) {
            throw new RuntimeException("Forward creation failed");
        }

        return ForwardMapper.toDto(forward);
    }
    @Transactional
    public ForwardDto updateForward(ForwardDto dto) {
        if (dto.methodId == null) {
            throw new IllegalArgumentException("Forward methodId is required for update");
        }
        Forward forward = ForwardMapper.toEntity(dto);
        forward.setMethodId(dto.methodId);
        forward.setCreatedAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now());

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
    public void deleteForward(UUID forwardId) {
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

    public ForwardDto getForwardById(UUID id) {
        try {
            Forward forward = forwardRepository.getForwardById(id);
            return ForwardMapper.toDto(forward);
        } catch (Exception e) {
            logger.error("Error getting forward by id {}", id, e);
            throw new RuntimeException("Error getting forward by id", e);
        }
    }
}