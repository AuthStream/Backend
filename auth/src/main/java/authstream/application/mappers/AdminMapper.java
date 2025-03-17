package authstream.application.mappers;

import authstream.application.dtos.AdminDto;
import authstream.domain.entities.Admin;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Chuyển List<Map<String, String>> thành chuỗi JSON
    public static String listToJsonString(List<Map<String, String>> list) {
        try {
            if (list == null || list.isEmpty()) {
                return "[]"; // Mảng rỗng
            }
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize list to JSON: " + e.getMessage());
        }
    }

    // Chuyển chuỗi JSON về List<Map<String, String>>
    public static List<Map<String, String>> jsonStringToList(String json) {
        try {
            if (json == null || json.equals("[]")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<Map<String, String>>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to list: " + e.getMessage());
        }
    }

    public static Admin toEntity(AdminDto dto) {
        if (dto == null) return null;
        Admin admin = new Admin();
        admin.setUsername(dto.username);
        admin.setPassword(dto.password);
        admin.setUri(dto.uri);
        admin.setDatabaseUsername(dto.databaseUsername);
        admin.setDatabasePassword(dto.databasePassword);
        admin.setDatabaseType(dto.databaseType);
        admin.setSslMode(dto.sslMode);
        admin.setHost(dto.host);
        admin.setPort(dto.port);
        admin.setConnectionString(dto.connectionString);
        admin.setTableIncludeList(listToJsonString(dto.tableIncludeList));
        admin.setSchemaIncludeList(listToJsonString(dto.schemaIncludeList));
        admin.setCollectionIncludeList(listToJsonString(dto.collectionIncludeList));
        return admin;
    }

    public static AdminDto toDto(Admin entity) {
        if (entity == null) return null;
        AdminDto dto = new AdminDto();
        dto.id = entity.getId();
        dto.username = entity.getUsername();
        dto.password = entity.getPassword();
        dto.uri = entity.getUri();
        dto.databaseUsername = entity.getDatabaseUsername();
        dto.databasePassword = entity.getDatabasePassword();
        dto.databaseType = entity.getDatabaseType();
        dto.sslMode = entity.getSslMode();
        dto.host = entity.getHost();
        dto.port = entity.getPort();
        dto.connectionString = entity.getConnectionString();
        dto.tableIncludeList = jsonStringToList(entity.getTableIncludeList());
        dto.schemaIncludeList = jsonStringToList(entity.getSchemaIncludeList());
        dto.collectionIncludeList = jsonStringToList(entity.getCollectionIncludeList());
        dto.createdAt = entity.getCreatedAt();
        dto.updatedAt = entity.getUpdatedAt();
        return dto;
    }
}