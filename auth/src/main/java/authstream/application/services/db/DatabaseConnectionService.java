package authstream.application.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

@Service
public class DatabaseConnectionService {

    public static Pair<Boolean, String> checkDatabaseConnection(String connectionString) {
        try (Connection connection = DriverManager.getConnection(connectionString)) {
            return Pair.of(true, "Connected successfully!");
        } catch (SQLException e) {
            return Pair.of(false, "Database connection failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String testConnectionString = "jdbc:postgresql://ep-snowy-fire-a831dkmt.eastus2.azure.neon.tech:5432/Linglooma?user=Linglooma_owner&password=npg_KZsn7Wl3LOdu&sslmode=require";
        Pair<Boolean, String> result = checkDatabaseConnection(testConnectionString);
        System.out.println("Success: " + result.getLeft());
        System.out.println("Message: " + result.getRight());
    }
}