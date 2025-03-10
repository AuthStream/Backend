package authstream.application.mappers;

import authstream.application.dtos.ForwardDto;
import authstream.domain.entities.Forward;

public class ForwardMapper {

    public static Forward toEntity(ForwardDto dto) {
        if (dto == null) {
            return null;
        }
        Forward forward = new Forward();
        forward.setApplicationId(dto.application_id != null ? dto.application_id : null);
        forward.setName(dto.name);
        forward.setProxyHostIp(dto.proxyHostIp);
        forward.setDomainName(dto.domainName);
        forward.setCallbackUrl(dto.callbackUrl);
        return forward;
    }

    public static ForwardDto toDto(Forward entity) {
        if (entity == null) {
            return null;
        }
        ForwardDto dto = new ForwardDto();
        dto.method_id = entity.getMethodId();
        dto.application_id = entity.getApplicationId();
        dto.name = entity.getName();
        dto.proxyHostIp = entity.getProxyHostIp();
        dto.domainName = entity.getDomainName();
        dto.callbackUrl = entity.getCallbackUrl();
        dto.createdAt = entity.getCreatedAt();
        return dto;
    }
}