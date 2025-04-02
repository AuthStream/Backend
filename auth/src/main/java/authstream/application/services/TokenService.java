package authstream.application.services;

import authstream.application.dtos.TokenDto;
import authstream.application.mappers.TokenMapper;
import authstream.domain.entities.Application;
import authstream.domain.entities.Token;
import authstream.infrastructure.repositories.ApplicationRepository;
import authstream.infrastructure.repositories.TokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import java.util.List;  // Thêm dòng này
import java.util.stream.Collectors;
@Service
public class TokenService {

    private final TokenRepository tokenRepository;
    private final ApplicationRepository applicationRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(TokenService.class);

    public TokenService(TokenRepository tokenRepository, ApplicationRepository applicationRepository) {
        this.tokenRepository = tokenRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public TokenDto createToken(TokenDto dto) {
        if (dto.getBody() == null) {
            throw new IllegalArgumentException("Token body is required");
        }
        if (dto.getEncryptToken() == null) {
            throw new IllegalArgumentException("Encrypt token is required");
        }
        if (dto.getExpiredDuration() == null) {
            throw new IllegalArgumentException("Expired duration is required");
        }

        Application application = null;
        if (dto.getApplicationId() != null) {
            application = applicationRepository.findById(dto.getApplicationId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Application with ID " + dto.getApplicationId() + " does not exist"));
        }

        Token token = TokenMapper.toEntity(dto);
        token.setId(UUID.randomUUID());
        token.setApplication(application);

        String bodyJson;
        try {
            bodyJson = objectMapper.writeValueAsString(dto.getBody());
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert token body to JSON", e);
        }
        int status = tokenRepository.addToken(
                token.getId(),
                token.getName(),
                bodyJson,
                token.getEncryptToken(),
                token.getExpiredDuration(),
                application != null ? application.getId() : null);
        if (status == 0) {
            throw new RuntimeException("Token creation failed");
        }

        return TokenMapper.toDto(token);
    }

    public TokenDto getTokenById(UUID id) {
        Token token = tokenRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Token with ID " + id + " not found"));
        return TokenMapper.toDto(token);
    }

    public List<TokenDto> getAllToken() { 
        logger.info("Fetching all tokens from database");
        List<TokenDto> tokens = tokenRepository.getAllTokens()
                .stream()
                .map(TokenMapper::toDto)
                .collect(Collectors.toList());
        logger.info("Retrieved {} tokens", tokens.size());
        return tokens;
    }

        public void delteToken(UUID id) {
        int result = tokenRepository.deleteTokenById(id);
        if (result != 1) {
            throw new RuntimeException("Failed to delete admin or admin not found");
        }
    }

}