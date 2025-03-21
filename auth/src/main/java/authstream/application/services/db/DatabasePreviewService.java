package authstream.application.services.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import authstream.application.services.db.DatabaseClass.Schema;
import authstream.application.services.db.DatabaseClass.Table;
import authstream.application.services.db.DatabaseClass.TableData;

public class DatabasePreviewService {

    public static List<TableData> previewData(String connectionString, List<Table> tables, Integer limit,
            Integer offset) throws SQLException {
        List<TableData> previewDataList = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(connectionString)) {
            for (Table table : tables) {
                String tableName = table.getTableName();
                String query = "SELECT * FROM " + tableName + " LIMIT ? OFFSET ?";

                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, limit);
                    stmt.setInt(2, offset);
                    ResultSet rs = stmt.executeQuery();

                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<Map<String, Object>> rows = new ArrayList<>();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = rs.getObject(i);
                            row.put(columnName, value);
                        }
                        rows.add(row);
                    }
                    previewDataList.add(new TableData(tableName, rows));
                }
            }
        }

        return previewDataList;
    }

    public static void main(String[] args) {
        String connectionString = "jdbc:postgresql://ep-snowy-fire-a831dkmt.eastus2.azure.neon.tech:5432/Linglooma?user=Linglooma_owner&password=npg_KZsn7Wl3LOdu&sslmode=require";
        try {
            Schema schema = DatabaseSchema.viewSchema(connectionString);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);

            List<Table> tables = schema.getDatabaseSchema();
            Integer limit = 2;
            Integer offset = 0;
            for (Table table : tables) {
                String tableName = table.getTableName();
                System.out.println("Table: " + table);
            }
            List<TableData> previewData = previewData(connectionString, tables, limit, offset);
            String previewJson = mapper.writeValueAsString(previewData);
            System.out.println("\nPreview Data:");
            System.out.println(previewJson);

        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Serialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
