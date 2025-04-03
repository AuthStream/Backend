package authstream.presentation.controllers;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.ApiResponse;
import authstream.application.dtos.CustomMessagesDto;
import authstream.application.services.CustomMessagesService;

@RestController
@RequestMapping("/api/custom-messages")
public class CustomMessagesController {

    private final CustomMessagesService customMessagesService;

    public CustomMessagesController(CustomMessagesService customMessagesService) {
        this.customMessagesService = customMessagesService;
    }

    // Create
    @PostMapping
    public ResponseEntity<ApiResponse> createCustomMessage(@RequestBody CustomMessagesDto dto) {
        try {
            Pair<CustomMessagesDto, Object> createdPair = customMessagesService.createCustomMessage(dto);
            if (createdPair.getRight() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, createdPair.getRight().toString()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
                    createdPair.getLeft(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                    null, e.getMessage()));
        }
    }

    // Read all
    @GetMapping
    public ResponseEntity<ApiResponse> getAllCustomMessages() {

        try {
            Pair<List<CustomMessagesDto>, Object> messagePair = customMessagesService.getAllCustomMessages();
            if (messagePair.getRight() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, messagePair.getRight().toString()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
                    messagePair.getLeft(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                    null, e.getMessage()));
        }

    }

    // Read by ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getCustomMessageById(@PathVariable UUID id) {

        try {
            Pair<CustomMessagesDto, Object> getMessageByIdPair = customMessagesService.getCustomMessageById(id);
            if (getMessageByIdPair.getRight() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, getMessageByIdPair.getRight().toString()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
                    getMessageByIdPair.getLeft(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                    null, e.getMessage()));
        }

    }

    // Update
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateCustomMessage(
            @PathVariable UUID id,
            @RequestBody CustomMessagesDto dto) {
        try {
            dto.setMessageId(id);
            Pair<CustomMessagesDto, Object> updatedPair = customMessagesService.updateCustomMessage(dto);
            if (updatedPair.getRight() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, updatedPair.getRight().toString()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
                    updatedPair.getLeft(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                    null, e.getMessage()));
        }
    }

    // Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteCustomMessage(@PathVariable UUID id) {
        try {
            Pair<Boolean, Object> deletePair = customMessagesService.deleteCustomMessage(id);
            if (deletePair.getRight() != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse(null, deletePair.getRight().toString()));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse(
                    deletePair.getLeft(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(
                    null, e.getMessage()));
        }
    }
}