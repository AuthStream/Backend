package authstream.application.services.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSchema {

    public static class Schema {
        private String databaseName;
        private List<Table> databaseSchema;

        public Schema(String databaseName, List<Table> databaseSchema) {
            this.databaseName = databaseName;
            this.databaseSchema = databaseSchema;
        }

        public String getDatabaseName() {
            return databaseName;
        }

        public List<Table> getDatabaseSchema() {
            return databaseSchema;
        }
    }

    public static class Table {
        private String tableName;
        private List<Column> columns;

        public Table(String tableName, List<Column> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public String getTableName() {
            return tableName;
        }

        public List<Column> getColumns() {
            return columns;
        }
    }

    public static class Column {
        private String name;
        private String type;
        private List<String> constraints;
        private Reference referenceTo;

        public Column(String name, String type, List<String> constraints, Reference referenceTo) {
            this.name = name;
            this.type = type;
            this.constraints = constraints;
            this.referenceTo = referenceTo;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public List<String> getConstraints() {
            return constraints;
        }

        public Reference getReferenceTo() {
            return referenceTo;
        }
    }

    public static class Reference {
        private String tableName;
        private String columnName;

        public Reference(String tableName, String columnName) {
            this.tableName = tableName;
            this.columnName = columnName;
        }

        public String getTableName() {
            return tableName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public static Schema viewSchema(String connectionString) throws SQLException {
        try (Connection conn = DriverManager.getConnection(connectionString)) {
            DatabaseMetaData metaData = conn.getMetaData();
            String databaseName = conn.getCatalog();

            List<Table> tablesList = new ArrayList<>();
            ResultSet tables = metaData.getTables(null, null, "%", new String[]{"TABLE"});

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