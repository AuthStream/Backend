package authstream.application.services.db;

import org.apache.commons.lang3.tuple.Pair;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataReplicationService {

    public static Pair<Boolean, String> replicateData(String sourceConnectionString, String destinationConnectionString) {
        try (Connection sourceConn = DriverManager.getConnection(sourceConnectionString);
             Connection destConn = DriverManager.getConnection(destinationConnectionString)) {

            DatabaseMetaData sourceMetaData = sourceConn.getMetaData();
            ResultSet tables = sourceMetaData.getTables(null, null, "%", new String[]{"TABLE"});
            List<String> tableNames = new ArrayList<>();

            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
            tables.close();

            if (tableNames.isEmpty()) {
                return Pair.of(false, "No tables found in source database");
            }

            Statement destStmt = destConn.createStatement();
            for (String tableName : tableNames) {
                try {
                    destStmt.execute("DROP TABLE IF EXISTS " + tableName + " CASCADE");
                } catch (SQLException e) {
                    return Pair.of(false, "Failed to drop table " + tableName + " in destination: " + e.getMessage());
                }

                ResultSet columns = sourceMetaData.getColumns(null, null, tableName, "%");
                StringBuilder createTableSql = new StringBuilder("CREATE TABLE " + tableName + " (");
                List<String> columnDefs = new ArrayList<>();
                List<String> primaryKeys = new ArrayList<>();

                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    int columnSize = columns.getInt("COLUMN_SIZE");
                    String nullable = columns.getString("IS_NULLABLE");

                    String columnDef = columnName + " " + columnType;
                    if (columnSize > 0 && columnType.toUpperCase().contains("CHAR")) {
                        columnDef += "(" + columnSize + ")";
                    }
                    if ("NO".equalsIgnoreCase(nullable)) {
                        columnDef += " NOT NULL";
                    }
                    columnDefs.add(columnDef);
                }
                columns.close();

                ResultSet pk = sourceMetaData.getPrimaryKeys(null, null, tableName);
                while (pk.next()) {
                    primaryKeys.add(pk.getString("COLUMN_NAME"));
                }
                pk.close();
                if (!primaryKeys.isEmpty()) {
                    columnDefs.add("PRIMARY KEY (" + String.join(", ", primaryKeys) + ")");
                }

                createTableSql.append(String.join(", ", columnDefs)).append(")");
                try {
                    destStmt.execute(createTableSql.toString());
                } catch (SQLException e) {
                    return Pair.of(false, "Failed to create table " + tableName + " in destination: " + e.getMessage());
                }

                String selectQuery = "SELECT * FROM " + tableName;
                try (PreparedStatement sourceStmt = sourceConn.prepareStatement(selectQuery);
                     ResultSet rs = sourceStmt.executeQuery()) {

                    int columnCount = rs.getMetaData().getColumnCount();
                    String insertQuery = "INSERT INTO " + tableName + " VALUES (" + "?, ".repeat(columnCount - 1) + "?)";
                    try (PreparedStatement insertStmt = destConn.prepareStatement(insertQuery)) {
                        while (rs.next()) {
                            for (int i = 1; i <= columnCount; i++) {
                                insertStmt.setObject(i, rs.getObject(i));
                            }
                            insertStmt.addBatch();
                        }
                        insertStmt.executeBatch();
                    }
                } catch (SQLException e) {
                    return Pair.of(false, "Failed to copy data for table " + tableName + ": " + e.getMessage());
                }
            }

            destStmt.close();
            return Pair.of(true, "");

        } catch (SQLException e) {
            return Pair.of(false, "Database connection failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        String destination = "jdbc:postgresql://localhost:5432/authdb?user=postgres&password=123456";
        String source = "jdbc:postgresql://ep-snowy-fire-a831dkmt.eastus2.azure.neon.tech:5432/Linglooma?user=Linglooma_owner&password=npg_KZsn7Wl3LOdu&sslmode=require";

        Pair<Boolean, String> result = replicateData(source, destination);
        if (result.getLeft()) {
            System.out.println("Data replication successful!");
        } else {
            System.err.println("Data replication failed: " + result.getRight());
        }
    }
}