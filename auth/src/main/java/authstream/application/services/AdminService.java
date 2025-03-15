package authstream.application.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import authstream.application.dtos.AdminDto;
import authstream.application.mappers.AdminMapper;
import authstream.domain.entities.Admin;
import authstream.infrastructure.repositories.AdminRepository;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    public AdminDto createAdmin(AdminDto dto) {
        Admin admin = AdminMapper.toEntity(dto);
        UUID id = UUID.randomUUID(); // Sinh UUID trong service vì native query không tự sinh
        admin.setId(id);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());

        int result = adminRepository.createAdmin(
            admin.getId(),
            admin.getUsername(),
            admin.getPassword(),
            admin.getUri(),
            admin.getDatabaseUsername(),
            admin.getDatabasePassword(),
            admin.getDatabaseType().name(),
            admin.getSslMode() != null ? admin.getSslMode().name() : null,
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
        admin.setUri(dto.getUri() != null ? dto.getUri() : admin.getUri());
        admin.setDatabaseUsername(dto.getDatabaseUsername() != null ? dto.getDatabaseUsername() : admin.getDatabaseUsername());
        admin.setDatabasePassword(dto.getDatabasePassword() != null ? dto.getDatabasePassword() : admin.getDatabasePassword());
        admin.setDatabaseType(dto.getDatabaseType() != null ? dto.getDatabaseType() : admin.getDatabaseType());
        admin.setSslMode(dto.getSslMode() != null ? dto.getSslMode() : admin.getSslMode());
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