package authstream.application.mappers;

import java.util.ArrayList;
import java.util.List;

import authstream.application.dtos.AdminDto;
import authstream.domain.entities.Admin;
public class AdminMapper {

    // Hàm thủ công chuyển List<String> thành chuỗi JSON
    public static String listToJsonString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "[]"; // Mảng rỗng
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Hàm thủ công chuyển chuỗi JSON về List<String> (nếu cần)
    public static List<String> jsonStringToList(String json) {
        if (json == null || json.equals("[]")) {
            return new ArrayList<>();
        }
        // Loại bỏ dấu [ và ], tách chuỗi theo dấu phẩy
        String trimmed = json.substring(1, json.length() - 1); // Bỏ [ và ]
        if (trimmed.isEmpty()) {
            return new ArrayList<>();
        }
        String[] elements = trimmed.split(",");
        List<String> result = new ArrayList<>();
        for (String element : elements) {
            // Loại bỏ dấu nháy kép
            result.add(element.substring(1, element.length() - 1));
        }
        return result;
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