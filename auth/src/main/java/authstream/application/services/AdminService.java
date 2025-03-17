package authstream.application.services;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authstream.application.dtos.AdminDto;
import authstream.application.mappers.AdminMapper;
import authstream.domain.entities.Admin;
import authstream.infrastructure.repositories.AdminRepository;
import authstream.utils.ValidStringDb;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminDto createAdmin(AdminDto dto) {
        Admin admin = AdminMapper.toEntity(dto);
        UUID id = UUID.randomUUID(); 
        admin.setId(id);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        String connectionString = admin.getConnectionString();
        if (connectionString == null || connectionString.isEmpty()) {

            if (admin.getDatabaseUsername() == null || admin.getDatabaseUsername().isEmpty()) {
                throw new IllegalArgumentException("Database username is required when connectionString is not provided");
            }
            if (admin.getDatabasePassword() == null || admin.getDatabasePassword().isEmpty()) {
                throw new IllegalArgumentException("Database password is required when connectionString is not provided");
            }
            if (admin.getUri() == null || admin.getUri().isEmpty()) {
                throw new IllegalArgumentException("URI is required when connectionString is not provided");
            }
            if(ValidStringDb.checkUri(admin.getUri()) == false) {
                throw new IllegalArgumentException("Invalid URI format");
            }
            if (admin.getDatabaseType() == null) {
                throw new IllegalArgumentException("Database type is required when connectionString is not provided");
            }
            if (admin.getPort() == null) {
                throw new IllegalArgumentException("Port is required when connectionString is not provided");
            }

            if (admin.getHost() == null) {
                throw new IllegalArgumentException("Host is required when connectionString is not provided");
            }

            String host = null;
            String dbname;


            try {
                URI uri = new URI(admin.getUri());
                host = uri.getHost() != null ? uri.getHost() : admin.getHost();
                dbname = uri.getPath() != null ? uri.getPath().replaceFirst("/", "") : "";
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URI format: " + e.getMessage());
            }

            if (host == null || host.isEmpty()) {
                throw new IllegalArgumentException("Host is required in URI");
            }

            int port = admin.getPort();
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }

            String sslModeParam = admin.getSslMode() != null ? admin.getSslMode().name().toLowerCase() : "disabled";
            switch (admin.getDatabaseType()) {
                case MYSQL:
                    String useSSL = sslModeParam.equals("disabled") ? "false" : "true";
                    connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=%s",
                            host, port, dbname, admin.getDatabaseUsername(), admin.getDatabasePassword(), useSSL);
                    break;
                case POSTGRESQL:
                    connectionString = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s&sslmode=%s",
                            host, port, dbname, admin.getDatabaseUsername(), admin.getDatabasePassword(), sslModeParam);
                    break;
                case MONGODB:
                    throw new IllegalArgumentException("MongoDB connection not supported via JDBC");
                default:
                    throw new IllegalArgumentException("Unsupported database type");
            }

            admin.setConnectionString(connectionString);
        }

        int result = adminRepository.createAdmin(
            admin.getId(),
            admin.getUsername(),
            admin.getPassword(),
            admin.getUri(),
            admin.getDatabaseUsername(),
            admin.getDatabasePassword(),
            admin.getDatabaseType().name(),
            admin.getSslMode() != null ? admin.getSslMode().name() : null,
            admin.getHost(),
            admin.getPort(),
            admin.getConnectionString(),
            admin.getTableIncludeList(),
            admin.getSchemaIncludeList(),
            admin.getCollectionIncludeList(),
            admin.getCreatedAt(),
            admin.getUpdatedAt()
        );
        if (result != 1) {
            throw new RuntimeException("Failed to create admin");
        }
        return AdminMapper.toDto(admin);
    }
    public AdminDto updateAdmin(UUID id, AdminDto dto) {
        Admin admin = adminRepository.findAdminById(id)
            .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setUsername(dto.getUsername() != null ? dto.getUsername():  admin.getUsername() );
        admin.setPassword(dto.getPassword() != null ? dto.getPassword() : admin.getPassword());
   
        if(ValidStringDb.checkUri(admin.getUri()) == false) {
            throw new IllegalArgumentException("Invalid URI format");
        }
        admin.setUri(dto.getUri() != null ? dto.getUri() : admin.getUri());
        admin.setDatabaseUsername(dto.getDatabaseUsername() != null ? dto.getDatabaseUsername() : admin.getDatabaseUsername());
        admin.setDatabasePassword(dto.getDatabasePassword() != null ? dto.getDatabasePassword() : admin.getDatabasePassword());
        admin.setDatabaseType(dto.getDatabaseType() != null ? dto.getDatabaseType() : admin.getDatabaseType());
        admin.setSslMode(dto.getSslMode() != null ? dto.getSslMode() : admin.getSslMode());
        admin.setPort(dto.getPort() != null ? dto.getPort() : admin.getPort());
        admin.setConnectionString(dto.getConnectionString() != null ? dto.getConnectionString() : admin.getConnectionString());
        admin.setTableIncludeList(dto.getTableIncludeList() != null ? AdminMapper.listToJsonString(dto.getTableIncludeList()) : admin.getTableIncludeList());
        admin.setSchemaIncludeList(dto.getSchemaIncludeList() != null ? AdminMapper.listToJsonString(dto.getSchemaIncludeList()) : admin.getSchemaIncludeList());
        admin.setCollectionIncludeList(dto.getCollectionIncludeList() != null ? AdminMapper.listToJsonString(dto.getCollectionIncludeList()) : admin.getCollectionIncludeList());

        admin.setUpdatedAt(LocalDateTime.now());

        int result = adminRepository.updateAdmin(
            id,
            admin.getUsername(),
            admin.getPassword(),
            admin.getUri(),
            admin.getDatabaseUsername(),
            admin.getDatabasePassword(),
            admin.getDatabaseType().name(),
            admin.getSslMode() != null ? admin.getSslMode().name() : null,
            admin.getHost(),
            admin.getPort(),
            admin.getConnectionString(),
            admin.getTableIncludeList(),
            admin.getSchemaIncludeList(),
            admin.getCollectionIncludeList(),
            admin.getUpdatedAt()
        );
        if (result != 1) {
            throw new RuntimeException("Failed to update admin");
        }
        return AdminMapper.toDto(admin);
    }

    public void deleteAdmin(UUID id) {
        int result = adminRepository.deleteAdmin(id);
        if (result != 1) {
            throw new RuntimeException("Failed to delete admin or admin not found");
        }
    }

    public AdminDto getAdminById(UUID id) {
        Admin admin = adminRepository.findAdminById(id)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        return AdminMapper.toDto(admin);
    }

    public List<AdminDto> getAllAdmins() {
        return adminRepository.findAllAdmins().stream()
            .map(AdminMapper::toDto)
            .toList();
    }

    public AdminDto getAdminByUsername(String username) {
        Admin admin = adminRepository.findAdminByUsername(username)
            .orElseThrow(() -> new RuntimeException("Admin not found"));
        return AdminMapper.toDto(admin);
    }
}