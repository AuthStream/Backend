package authstream.application.mappers;

import authstream.application.dtos.TokenDto;
import authstream.domain.entities.Application;
import authstream.domain.entities.Token;

public class TokenMapper {

    public static Token toEntity(TokenDto dto) {
        if (dto == null) {
            return null;
        }
        Token token = new Token();
        token.setName(dto.getName());
        token.setBody(dto.getBody());
        token.setEncryptToken(dto.getEncryptToken());
        token.setExpiredDuration(dto.getExpiredDuration());
        if (dto.getApplicationId() != null) {
            Application application = new Application();
            application.setId(dto.getApplicationId());
            token.setApplication(application);
        }
        return token;
    }

    public static TokenDto toDto(Token entity) {
        if (entity == null) {
            return null;
        }
        TokenDto dto = new TokenDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBody(entity.getBody());
        dto.setEncryptToken(entity.getEncryptToken());
        dto.setExpiredDuration(entity.getExpiredDuration());
        dto.setApplicationId(entity.getApplication() != null ? entity.getApplication().getId() : null);
        return dto;
    }
}