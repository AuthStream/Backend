package authstream.application.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import org.apache.commons.lang3.tuple.Pair;

public class SqlPreviewService {

    public static Pair<String, String> previewSQL(String connectionString, String query) {
        int affectedRows = 0;
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            conn.setAutoCommit(false);
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                boolean isResultSet = stmt.execute();

                if (isResultSet) {
                    ResultSet rs = stmt.getResultSet();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    for (int i = 1; i <= columnCount; i++) {
                        System.out.printf("%-20s", metaData.getColumnName(i));
                    }
                    sb.append("\n");
                    int rowCount = 0;
                    while (rs.next()) {
                        rowCount++;
                        for (int i = 1; i <= columnCount; i++) {
                            Object value = rs.getObject(i);
                            System.out.printf("%-20s", value != null ? value.toString() : "NULL");
                        }
                    }
                    sb.append("Rows retrieved: " + rowCount);
                    affectedRows = rowCount;
                } else {
                    affectedRows = stmt.getUpdateCount();
                    sb.append("Rows affected: " + affectedRows);
                }
            }
            conn.rollback();
        } catch (SQLException e) {
            return Pair.of("Failed to execute query: " + e.getMessage(), null);
        }

        return Pair.of(sb.toString(), Integer.toString(affectedRows));
    }

    public static void main(String[] args) {
        String connectionString = "jdbc:postgresql://ep-snowy-fire-a831dkmt.eastus2.azure.neon.tech:5432/Linglooma?user=Linglooma_owner&password=npg_KZsn7Wl3LOdu&sslmode=require";
        
        String[] queries = {
            "SELECT * FROM students LIMIT 2",                            
            "INSERT INTO students (name) VALUES ('newuser')",
            "UPDATE students SET name = 'updated' WHERE student_id = 1",
            "DELETE FROM students WHERE name = 'newuser'",
            "DELETE * from students where 1 = 1"
        };

        for (String query : queries) {
            Pair<String, String> result = previewSQL(connectionString, query);
            System.out.println("Query: " + query);
            System.out.println("Result: " + result.getLeft());
            System.out.println("Affected rows: " + result.getRight());
            System.out.println();
        }
    }
}