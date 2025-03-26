package authstream.application.mappers;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import authstream.application.dtos.AdminDto;
import authstream.application.dtos.TableInfo;
import authstream.domain.entities.Admin;

public class AdminMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Chuyển List thành chuỗi JSON
    public static String listToJsonString(List<?> list) {
        try {
            if (list == null || list.isEmpty()) {
                return "[]"; // Mảng rỗng
            }
            return objectMapper.writeValueAsString(list);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize list to JSON: " + e.getMessage());
        }
    }

    // Chuyển chuỗi JSON về List<TableInfo>
    public static List<TableInfo> jsonStringToList(String json) {
        try {
            if (json == null || json.equals("[]")) {
                return new ArrayList<>();
            }
            return objectMapper.readValue(json, new TypeReference<List<TableInfo>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse JSON to list: " + e.getMessage());
        }
    }

    public static Admin toEntity(AdminDto dto) {
        if (dto == null) return null;
        Admin admin = new Admin();
        admin.setId(dto.getId());
        admin.setUsername(dto.getUsername());
        admin.setPassword(dto.getPassword());
        admin.setUri(dto.getUri());
        admin.setDatabaseUsername(dto.getDatabaseUsername());
        admin.setDatabasePassword(dto.getDatabasePassword());
        admin.setDatabaseType(dto.getDatabaseType());
        admin.setSslMode(dto.getSslMode());
        admin.setHost(dto.getHost());
        admin.setPort(dto.getPort());
        admin.setConnectionString(dto.getConnectionString());
        admin.setTableIncludeList(listToJsonString(dto.getTableIncludeList()));
        admin.setSchemaIncludeList(listToJsonString(dto.getSchemaIncludeList()));
        admin.setCollectionIncludeList(listToJsonString(dto.getCollectionIncludeList()));
        admin.setCreatedAt(dto.getCreatedAt());
        admin.setUpdatedAt(dto.getUpdatedAt());
        return admin;
    }

    public static AdminDto toDto(Admin entity) {
        if (entity == null) return null;
        AdminDto dto = new AdminDto();
        dto.setId(entity.getId());
        dto.setUsername(entity.getUsername());
        dto.setPassword(entity.getPassword());
        dto.setUri(entity.getUri());
        dto.setDatabaseUsername(entity.getDatabaseUsername());
        dto.setDatabasePassword(entity.getDatabasePassword());
        dto.setDatabaseType(entity.getDatabaseType());
        dto.setSslMode(entity.getSslMode());
        dto.setHost(entity.getHost());
        dto.setPort(entity.getPort());
        dto.setConnectionString(entity.getConnectionString());
        dto.setTableIncludeList(jsonStringToList(entity.getTableIncludeList()));
        dto.setSchemaIncludeList(jsonStringToList(entity.getSchemaIncludeList()));
        dto.setCollectionIncludeList(jsonStringToList(entity.getCollectionIncludeList()));
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}