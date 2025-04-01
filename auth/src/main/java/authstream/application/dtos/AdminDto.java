package authstream.application.dtos;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import authstream.domain.entities.DatabaseType;
import authstream.domain.entities.SSL_Mode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDto {
    private UUID id;
    private String username;
    private String password;
    private String uri;
    private String databaseUsername;
    private String databasePassword;
    private DatabaseType databaseType;
    private SSL_Mode sslMode;
    private String host;
    private Integer port;
    private String connectionString;
    private List<TableInfo> tableIncludeList;
    private List<TableInfo> schemaIncludeList;
    private List<TableInfo> collectionIncludeList;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "AdminDto{databaseUsername='" + databaseUsername + "'}";
    }
}