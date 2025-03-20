package authstream.application.services.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import authstream.application.services.db.DatabaseClass.Column;
import authstream.application.services.db.DatabaseClass.Reference;
import authstream.application.services.db.DatabaseClass.Schema;
import authstream.application.services.db.DatabaseClass.Table;

public class DatabaseSchema {

    public static Schema viewSchema(String connectionString) throws SQLException {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseName = conn.getCatalog();

            List<Table> tablesList = new ArrayList<>();
            ResultSet tables = metaData.getTables(null, null, "%", new String[] { "TABLE" });

            while (tables.next()) {
                String tableName = tables.getString("TABLE_NAME");
                List<Column> columnsList = new ArrayList<>();

                // Get columns
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    String nullable = columns.getString("IS_NULLABLE");
                    List<String> constraints = new ArrayList<>();

                    if ("NO".equalsIgnoreCase(nullable)) {
                        constraints.add("NOT NULL");
                    }

                    ResultSet pk = metaData.getPrimaryKeys(null, null, tableName);
                    while (pk.next()) {
                        if (columnName.equals(pk.getString("COLUMN_NAME"))) {
                            constraints.add("PRIMARY KEY");
                            if ("SERIAL".equalsIgnoreCase(columnType) || "BIGSERIAL".equalsIgnoreCase(columnType)) {
                                constraints.add("AUTO_INCREMENT");
                            }
                        }
                    }
                    pk.close();

                    Reference reference = null;
                    ResultSet fk = metaData.getImportedKeys(null, null, tableName);
                    while (fk.next()) {
                        if (columnName.equals(fk.getString("FKCOLUMN_NAME"))) {
                            String pkTableName = fk.getString("PKTABLE_NAME");
                            String pkColumnName = fk.getString("PKCOLUMN_NAME");
                            reference = new Reference(pkTableName, pkColumnName);
                        }
                    }
                    fk.close();

                    columnsList.add(new Column(columnName, columnType, constraints, reference));
                }
                columns.close();

                tablesList.add(new Table(tableName, columnsList));
            }
            tables.close();

            return new Schema(databaseName, tablesList);
        }
    }

    public static void main(String[] args) {
        String connectionString = "jdbc:postgresql://ep-snowy-fire-a831dkmt.eastus2.azure.neon.tech:5432/Linglooma?user=Linglooma_owner&password=npg_KZsn7Wl3LOdu&sslmode=require";
        try {
            Schema schema = viewSchema(connectionString);
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            String json = mapper.writeValueAsString(schema);
            System.out.println(json);

        } catch (SQLException e) {
            System.err.println("Failed to retrieve schema: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to serialize schema to JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }
}