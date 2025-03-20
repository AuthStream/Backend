package authstream.utils;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.tuple.Pair;

import authstream.application.dtos.AdminDto;

public class validString {
    
public static Pair<String, Boolean> checkValidData(AdminDto adminDto) {        
        if (adminDto.getDatabaseUsername() == null || adminDto.getDatabaseUsername().isEmpty()) {
            return Pair.of("Database username is required", false);
        }
        if (adminDto.getDatabasePassword() == null || adminDto.getDatabasePassword().isEmpty()) {
            return Pair.of("Database password is required", false);
        }
        if (adminDto.getUri() == null || adminDto.getUri().isEmpty()) {
            return Pair.of("URI is required",false);
        }
        if(!ValidStringDb.checkUri(adminDto.getUri())){
            return Pair.of("Invalid URI format",false);
        }
        if (adminDto.getDatabaseType() == null) {
            return Pair.of("Database type is required",false);
        }
        if (adminDto.getSslMode() == null) {
            return Pair.of("SSL mode is required",false);
        }
        if (adminDto.getPort() == null) {
            return Pair.of("Port is required",false);
        }


        return  Pair.of("check Successfully",true);
    }
    

    public static String buildConnectionString(AdminDto adminDto) throws IllegalArgumentException {
        String host;
        String dbname;
        try {
            URI uri = new URI(adminDto.getUri());
            host = uri.getHost();
            dbname = uri.getPath() != null ? uri.getPath().replaceFirst("/", "") : "";
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid URI format: " + e.getMessage());
        }

        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Host is required in URI");
        }

        int port;
        try {
            port = adminDto.getPort();
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("Port must be between 1 and 65535");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid port value: " + e.getMessage());
        }

        String sslModeParam = adminDto.getSslMode().name().toLowerCase();
        String connectionString;
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
                throw new IllegalArgumentException("MongoDB connection not supported via JDBC");
            default:
                throw new IllegalArgumentException("Unsupported database type");
        }

        return connectionString;
    }
}
