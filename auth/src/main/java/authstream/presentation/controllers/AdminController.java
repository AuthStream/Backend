package authstream.presentation.controllers;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
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

import authstream.application.dtos.AdminDto;
import authstream.application.dtos.PreviewDataRequestDto;
import authstream.application.services.AdminService;
import authstream.application.services.db.DatabaseConnectionService;
import authstream.application.services.db.DatabasePreviewService;
import authstream.application.services.db.DatabasePreviewService.TableData;
import authstream.application.services.db.DatabaseSchema;
import authstream.application.services.db.DatabaseSchema.Table;
import authstream.utils.ValidStringDb;

@RestController
@RequestMapping("/admins/config")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService ) {
        this.adminService = adminService;
    }

    @PostMapping
    public ResponseEntity<AdminDto> createAdmin(@RequestBody AdminDto adminDto) {
        AdminDto createdAdmin = adminService.createAdmin(adminDto);
        return new ResponseEntity<>(createdAdmin, HttpStatus.CREATED);
    }

   @GetMapping
  public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> admins = adminService.getAllAdmins();
        return new ResponseEntity<>(admins, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable UUID id) {
        AdminDto admin = adminService.getAdminById(id);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<AdminDto> getAdminByUsername(@PathVariable String username) {
        AdminDto admin = adminService.getAdminByUsername(username);
        return new ResponseEntity<>(admin, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminDto> updateAdmin(@PathVariable UUID id, @RequestBody AdminDto adminDto) {
        AdminDto updatedAdmin = adminService.updateAdmin(id, adminDto);
        return new ResponseEntity<>(updatedAdmin, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable UUID id) {
        adminService.deleteAdmin(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


@PostMapping("/checkconnection")
    public ResponseEntity<String> checkConnection(@RequestBody AdminDto adminDto) {

        String connectionString = adminDto.getConnectionString();
        if (connectionString != null && !connectionString.isEmpty()) {
            if(ValidStringDb.checkConnectionString(connectionString)){
        
                Pair<Boolean, String> result = DatabaseConnectionService.checkDatabaseConnection(connectionString);
                return new ResponseEntity<>(result.getRight(),
                        result.getLeft() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);   
            } else {
                return new ResponseEntity<>("Connection failed", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

    
        if (adminDto.getDatabaseUsername() == null || adminDto.getDatabaseUsername().isEmpty()) {
            return new ResponseEntity<>("Database username is required", HttpStatus.BAD_REQUEST);
        }
        if (adminDto.getDatabasePassword() == null || adminDto.getDatabasePassword().isEmpty()) {
            return new ResponseEntity<>("Database password is required", HttpStatus.BAD_REQUEST);
        }
        if (adminDto.getUri() == null || adminDto.getUri().isEmpty()) {
            return new ResponseEntity<>("URI is required", HttpStatus.BAD_REQUEST);
        }
        if(!ValidStringDb.checkUri(adminDto.getUri())){
            return new ResponseEntity<>("Invalid URI format", HttpStatus.BAD_REQUEST);
        }
        if (adminDto.getDatabaseType() == null) {
            return new ResponseEntity<>("Database type is required", HttpStatus.BAD_REQUEST);
        }
        if (adminDto.getSslMode() == null) {
            return new ResponseEntity<>("SSL mode is required", HttpStatus.BAD_REQUEST);
        }
        if (adminDto.getPort() == null) {
            return new ResponseEntity<>("Port is required", HttpStatus.BAD_REQUEST);
        }

        String host;
        String dbname;
        try {
            URI uri = new URI(adminDto.getUri());
            host = uri.getHost();
            dbname = uri.getPath() != null ? uri.getPath().replaceFirst("/", "") : "";
        } catch (URISyntaxException e) {
            return new ResponseEntity<>("Invalid URI format: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        if (host == null || host.isEmpty()) {
            return new ResponseEntity<>("Host is required in URI", HttpStatus.BAD_REQUEST);
        }

        int port;
        try {
            port = adminDto.getPort();
            if (port <= 0 || port > 65535) {
                return new ResponseEntity<>("Port must be between 1 and 65535", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("Invalid port value: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        // create connection String
       String sslModeParam = adminDto.getSslMode().name().toLowerCase();
        switch (adminDto.getDatabaseType()) {
            case MYSQL:
                String useSSL = sslModeParam.equals("disabled") ? "false" : "true";
                connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=%s",
                        host, port, dbname, adminDto.getDatabaseUsername(), adminDto.getDatabasePassword(), useSSL);
                break;
            case POSTGRESQL:
                connectionString = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s&sslmode=%s",
                        host, port, dbname, adminDto.getDatabaseUsername(), adminDto.getDatabasePassword(), sslModeParam);
                break;
            case MONGODB: 
                return new ResponseEntity<>("MongoDB connection not supported via JDBC", HttpStatus.BAD_REQUEST);
            default:
                return new ResponseEntity<>("Unsupported database type", HttpStatus.BAD_REQUEST);
        }

        Pair<Boolean, String> result = DatabaseConnectionService.checkDatabaseConnection(connectionString);

        if (result.getLeft()) {

            return new ResponseEntity<>(result.getRight(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(result.getRight(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


   @PostMapping("/viewschema")
    public ResponseEntity<?> viewSchema(@RequestBody AdminDto adminDto) {
        try {

            String connectionString = adminDto.getConnectionString();
            if (connectionString != null && !connectionString.isEmpty()) {
                if(ValidStringDb.checkConnectionString(connectionString)){
                    DatabaseSchema.Schema schema = DatabaseSchema.viewSchema(connectionString);
                    return new ResponseEntity<>(schema, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>("Unsupported database type", HttpStatus.BAD_REQUEST);
                }
            }

            // Nếu không có connectionString, tạo mới từ các field
            if (adminDto.getDatabaseUsername() == null || adminDto.getDatabaseUsername().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (adminDto.getDatabasePassword() == null || adminDto.getDatabasePassword().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (adminDto.getUri() == null || adminDto.getUri().isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if(!ValidStringDb.checkUri(adminDto.getUri())){
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (adminDto.getDatabaseType() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (adminDto.getSslMode() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            if (adminDto.getPort() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String host;
            String dbname;
            try {
                URI uri = new URI(adminDto.getUri());
                host = uri.getHost();
                dbname = uri.getPath() != null ? uri.getPath().replaceFirst("/", "") : "";
            } catch (URISyntaxException e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            if (host == null || host.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            int port;
            try {
                port = adminDto.getPort();
                if (port <= 0 || port > 65535) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            String sslModeParam = adminDto.getSslMode().name().toLowerCase();
            switch (adminDto.getDatabaseType()) {
                case MYSQL:
                    String useSSL = sslModeParam.equals("disabled") ? "false" : "true";
                    connectionString = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useSSL=%s",
                            host, port, dbname, adminDto.getDatabaseUsername(), adminDto.getDatabasePassword(), useSSL);
                    break;
                case POSTGRESQL:
                    connectionString = String.format("jdbc:postgresql://%s:%d/%s?user=%s&password=%s&sslmode=%s",
                            host, port, dbname, adminDto.getDatabaseUsername(), adminDto.getDatabasePassword(), sslModeParam);
                    break;
                case MONGODB:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                default:
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            DatabaseSchema.Schema schema = DatabaseSchema.viewSchema(connectionString);
            return new ResponseEntity<>(schema, HttpStatus.OK);
        } catch (SQLException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


@PostMapping("/preview-data")
public ResponseEntity<?> previewData(@RequestBody PreviewDataRequestDto request) {
    try {
        // Validate request
        if (request == null || request.getConnectionString() == null || request.getTables() == null) {
            return ResponseEntity.badRequest().body("Invalid request: connectionString and tables are required");
        }

        List<Table> tables = request.getTables();
        for (Table table : tables) {
            if (table.getTableName() == null || table.getTableName().isEmpty()) {
                return ResponseEntity.badRequest().body("Invalid request: tableName is required for all tables");
            }
        }

        List<TableData> previewData = DatabasePreviewService.previewData(
                request.getConnectionString(),
                tables,
                request.getLimit() != null ? request.getLimit() : 10,
                request.getOffset() != null ? request.getOffset() : 0
        );

        return ResponseEntity.ok(previewData);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
    }
}

}