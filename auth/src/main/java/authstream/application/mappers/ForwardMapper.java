package authstream.application.mappers;

import authstream.application.dtos.ForwardDto;
import authstream.domain.entities.Forward;
import java.time.LocalDateTime;
public class ForwardMapper {

    public static Forward toEntity(ForwardDto dto) {
        if (dto == null) {
            return null;
        }
        return Forward.builder()
                .methodId(dto.methodId)
                .applicationId(dto.applicationId)
                .name(dto.name)
                .proxyHostIp(dto.proxyHostIp)
                .domainName(dto.domainName)
                .callbackUrl(dto.callbackUrl)
                .createdAt(dto.createdAt != null ? dto.createdAt : LocalDateTime.now())
                .build();
    }

    public static ForwardDto toDto(Forward entity) {
        if (entity == null) {
            return null;
        }
        ForwardDto dto = new ForwardDto();
        dto.methodId = entity.getMethodId();
        dto.applicationId = entity.getApplicationId();
        dto.name = entity.getName();
        dto.proxyHostIp = entity.getProxyHostIp();
        dto.domainName = entity.getDomainName();
        dto.callbackUrl = entity.getCallbackUrl();
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }
}