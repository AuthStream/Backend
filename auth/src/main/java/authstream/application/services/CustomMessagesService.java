package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import authstream.application.dtos.CustomMessagesDto;
import authstream.application.mappers.CustomMessagesMapper;
import authstream.domain.entities.CustomMessages;
import authstream.infrastructure.repositories.ApplicationRepository;
import authstream.infrastructure.repositories.CustomMessagesRepository;

@Service
public class CustomMessagesService {

    private CustomMessagesRepository customMessagesRepository;

    private ApplicationRepository applicationRepository;

    public CustomMessagesService(CustomMessagesRepository customMessagesRepository,
            ApplicationRepository applicationRepository) {
        this.customMessagesRepository = customMessagesRepository;
        this.applicationRepository = applicationRepository;
    }

    @Transactional
    public Pair<CustomMessagesDto, Object> createCustomMessage(CustomMessagesDto dto) {
        // Validate DTO
        if (dto.getErrorCode() == null) {
            return Pair.of(null, "Error code is required");

        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {

            return Pair.of(null, "Message is required");

        }

        // Check if application exists
        if (dto.getApplicationId() != null && !applicationRepository.existsById(dto.getApplicationId())) {

            return Pair.of(null, "Application with ID " + dto.getApplicationId() + " not found");
        }

        // Map DTO to entity
        CustomMessages entity = CustomMessagesMapper.toEntity(dto);
        entity.setMessageId(UUID.randomUUID()); // Tạo ID mới
        entity.setCreatedAt(LocalDateTime.now());

        // Save to DB
        CustomMessages saved = customMessagesRepository.save(entity);
        return Pair.of(CustomMessagesMapper.toDto(saved), null);
    }

    public Pair<List<CustomMessagesDto>, Object> getAllCustomMessages() {
        List<CustomMessages> messages = customMessagesRepository.findAllCustomMessages();
        List<CustomMessagesDto> dtos = messages.stream()
                .map(CustomMessagesMapper::toDto)
                .collect(Collectors.toList());
        return Pair.of(dtos, null);
    }

    public Pair<CustomMessagesDto, Object> getCustomMessageById(UUID id) {
        if (id == null) {
            return Pair.of(null, "Custom message ID is required");
        }

        CustomMessages message = customMessagesRepository.findCustomMessageById(id);
        if (message == null) {
            return Pair.of(null, "Custom message with ID " + id + " not found");
        }
        return Pair.of(CustomMessagesMapper.toDto(message), null);
    }

    @Transactional
    public Pair<CustomMessagesDto, Object> updateCustomMessage(CustomMessagesDto dto) {
        // Validate DTO
        if (dto.getMessageId() == null) {
            return Pair.of(null, "Custom message ID is required for update");

        }

        if (dto.getErrorCode() == null) {
            return Pair.of(null, "Error code is required");

        }
        if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {

            return Pair.of(null, "Message is required");

        }

        // Check if custom message exists
        if (!customMessagesRepository.existsById(dto.getMessageId())) {
            return Pair.of(null, "Custom message with ID " + dto.getMessageId() + " not found");

        }

        // Check if application exists
        if (dto.getApplicationId() != null && !applicationRepository.existsById(dto.getApplicationId())) {
            return Pair.of(null, "Application with ID " + dto.getApplicationId() + " not found");

        }

        // Map DTO to entity
        CustomMessages entity = CustomMessagesMapper.toEntity(dto);

        // Update in DB
        int updatedRows = customMessagesRepository.updateCustomMessage(
                entity.getMessageId(),
                entity.getApplication() != null ? entity.getApplication().getId() : null,
                entity.getErrorCode(),
                entity.getMessage(),
                entity.getCreatedAt());
        if (updatedRows == 0) {
            return Pair.of(null, "Failed to update custom message: No rows affected");

        }

        // Fetch updated entity
        CustomMessages updated = customMessagesRepository.findCustomMessageById(dto.getMessageId());
        return Pair.of(CustomMessagesMapper.toDto(updated), null);
    }

    @Transactional
    public Pair<Boolean, Object> deleteCustomMessage(UUID id) {
        if (id == null) {
            return Pair.of(false, "Custom message ID is required for deletion");
        }

        if (!customMessagesRepository.existsById(id)) {
            return Pair.of(false, "Custom message with ID " + id + " not found");
        }

        int deletedRows = customMessagesRepository.deleteCustomMessageById(id);
        if (deletedRows == 0) {
            return Pair.of(false, "Failed to delete custom message: No rows affected");
        }
        return Pair.of(true, null);
    }
}