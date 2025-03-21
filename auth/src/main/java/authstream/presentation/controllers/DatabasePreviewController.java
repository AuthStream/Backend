package authstream.presentation.controllers;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import authstream.application.dtos.PreviewSQLRequestDto;
import authstream.application.dtos.PreviewSQLResponseDto;
import authstream.application.mappers.PreviewMapper;
import authstream.application.services.db.SqlPreviewService;
import authstream.utils.SqlSanitizer;

@RestController
public class DatabasePreviewController {

    @PostMapping("/preview-sql")
    public ResponseEntity<?> previewSql(@RequestBody PreviewSQLRequestDto request) {
        // Validate request
        if (request == null || request.getConnectionString() == null || request.getConnectionString().isEmpty()) {
            return ResponseEntity.badRequest().body("Connection string is required");
        }
        if (request.getQuery() == null || request.getQuery().isEmpty()) {
            return ResponseEntity.badRequest().body("SQL query is required");
        }
        String sanitizedQuery = SqlSanitizer.sanitizeQuery(request.getQuery());
        try {
            System.out.println("Sanitizing query: " + request.getQuery());
            sanitizedQuery = SqlSanitizer.sanitizeQuery(request.getQuery());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid query: " + e.getMessage());
        }
        Pair<String, String> result = SqlPreviewService.previewSQL(
                request.getConnectionString(),
                sanitizedQuery);

        PreviewSQLResponseDto response = PreviewMapper.toDto(result);

        if (response.getMessage().startsWith("Failed to execute query")) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.ok(response);
    }
}