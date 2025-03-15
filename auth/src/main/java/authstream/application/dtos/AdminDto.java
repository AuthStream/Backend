package authstream.application.dtos;

import authstream.domain.entities.DatabaseType;
import authstream.domain.entities.SSL_Mode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AdminDto {
    public UUID id;
    public String username;
    public String password;
    public String uri;
    public String databaseUsername;
    public String databasePassword;
    public DatabaseType databaseType;
    public SSL_Mode sslMode;
    public String connectionString;
    public List<String> tableIncludeList; 
    public List<String> schemaIncludeList; 
    public List<String> collectionIncludeList; 
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
}