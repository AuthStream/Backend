package authstream.application.services;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import authstream.application.dtos.AdminDto;
import authstream.application.mappers.AdminMapper;
import authstream.application.services.db.DatabaseConnectionService;
import authstream.domain.entities.Admin;
import authstream.infrastructure.repositories.AdminRepository;
import authstream.utils.ValidStringDb;
import authstream.utils.validString;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminDto createAdmin(AdminDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Admin DTO cannot be null");
        }

        Admin admin = AdminMapper.toEntity(dto);
        UUID id = UUID.randomUUID();
        admin.setId(id);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        String connectionString = admin.getConnectionString();
        if (connectionString == null || connectionString.isEmpty()) {
            Pair<String, Boolean> checkValid = validString.checkValidData(dto);
            if (!checkValid.getRight()) {
                throw new IllegalArgumentException(checkValid.getLeft());
            }

            // Nếu host không được set, lấy từ URI
            if (admin.getHost() == null || admin.getHost().isEmpty()) {
                try {
                    URI uri = new URI(admin.getUri());
                    String host = uri.getHost();
                    if (host == null || host.isEmpty()) {
                        throw new IllegalArgumentException("Host is required in URI");
                    }
                    admin.setHost(host);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid URI format: " + e.getMessage());
                }
            }

            Pair<String, Object> resultConnectionString = validString.buildConnectionString(dto);
            if (resultConnectionString.getRight() !=null) {
                admin.setConnectionString(resultConnectionString.getLeft());

            } else {
                admin.setConnectionString(null);

            }
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
                admin.getUpdatedAt());
        if (result != 1) {
            throw new RuntimeException("Failed to create admin");
        }
        return AdminMapper.toDto(admin);
    }

    public AdminDto updateAdmin(UUID id, AdminDto dto) {

        if (id == null) {
            throw new IllegalArgumentException("Admin ID cannot be null");
        }
        if (dto == null) {
            throw new IllegalArgumentException("Admin DTO cannot be null");
        }

        Admin admin = adminRepository.findAdminById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setUsername(dto.getUsername() != null ? dto.getUsername() : admin.getUsername());
        admin.setPassword(dto.getPassword() != null ? dto.getPassword() : admin.getPassword());

        if (ValidStringDb.checkUri(admin.getUri()) == false) {
            throw new IllegalArgumentException("Invalid URI format");
        }

        String connectionString = admin.getConnectionString();
        if (connectionString == null || connectionString.isEmpty()) {
            Pair<String, Boolean> checkValid = validString.checkValidData(dto);
            if (!checkValid.getRight()) {
                throw new IllegalArgumentException(checkValid.getLeft());
            }

            // Nếu host không được set, lấy từ URI
            if (admin.getHost() == null || admin.getHost().isEmpty()) {
                try {
                    URI uri = new URI(admin.getUri());
                    String host = uri.getHost();
                    if (host == null || host.isEmpty()) {
                        throw new IllegalArgumentException("Host is required in URI");
                    }
                    admin.setHost(host);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid URI format: " + e.getMessage());
                }
            }

             Pair<String, Object> resultConnectionString = validString.buildConnectionString(dto);
            if (resultConnectionString.getRight() != null) {
                admin.setConnectionString(resultConnectionString.getLeft());

            } else {
                admin.setConnectionString(null);

            }
        }

        Boolean checkConnectionString = ValidStringDb.checkConnectionString(admin.getConnectionString());
        if (checkConnectionString) {
            Pair<Boolean, String> result = DatabaseConnectionService.checkDatabaseConnection(connectionString);
            if (!result.getLeft()) {
                throw new IllegalArgumentException(
                        "Database connection failure, check your information or connection String", null);
            }
        }

        admin.setUri(dto.getUri() != null ? dto.getUri() : admin.getUri());
        admin.setDatabaseUsername(
                dto.getDatabaseUsername() != null ? dto.getDatabaseUsername() : admin.getDatabaseUsername());
        admin.setDatabasePassword(
                dto.getDatabasePassword() != null ? dto.getDatabasePassword() : admin.getDatabasePassword());
        admin.setDatabaseType(dto.getDatabaseType() != null ? dto.getDatabaseType() : admin.getDatabaseType());
        admin.setSslMode(dto.getSslMode() != null ? dto.getSslMode() : admin.getSslMode());
        admin.setPort(dto.getPort() != null ? dto.getPort() : admin.getPort());
        admin.setTableIncludeList(
                dto.getTableIncludeList() != null ? AdminMapper.listToJsonString(dto.getTableIncludeList())
                        : admin.getTableIncludeList());
        admin.setSchemaIncludeList(
                dto.getSchemaIncludeList() != null ? AdminMapper.listToJsonString(dto.getSchemaIncludeList())
                        : admin.getSchemaIncludeList());
        admin.setCollectionIncludeList(
                dto.getCollectionIncludeList() != null ? AdminMapper.listToJsonString(dto.getCollectionIncludeList())
                        : admin.getCollectionIncludeList());

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
                admin.getUpdatedAt());
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

}