package authstream.application.services.db;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class DatabaseClass {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Schema {
        private String databaseName;
        private List<Table> databaseSchema;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Table {
        private String tableName;
        private List<Column> columns;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {
        private String name;
        private String type;
        private List<String> constraints;
        private Reference referenceTo;

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

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Reference {
        private String tableName;
        private String columnName;
    }

      @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TableData {
        private String tableName;
        private List<Map<String, Object>> rows;
    }

}
