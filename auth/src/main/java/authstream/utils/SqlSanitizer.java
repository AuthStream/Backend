package authstream.utils; // Thay đổi nếu cần thành authstream.application.utils

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SqlSanitizer {

    // Danh sách từ khóa nguy hiểm
    private static final List<String> DANGEROUS_KEYWORDS = Arrays.asList(
            "DROP", "DELETE", "TRUNCATE", "INSERT", "UPDATE", "ALTER", "CREATE", "GRANT", "REVOKE",
            "EXEC", "EXECUTE", "UNION", "SHUTDOWN"
    );

    // Regex đơn giản hóa, tránh ký tự đặc biệt phức tạp
    private static final Pattern DANGEROUS_PATTERN = Pattern.compile(
            "(?i)" +                                    // Không phân biệt hoa/thường
            "\\b(" + String.join("|", DANGEROUS_KEYWORDS) + ")\\b" + // Từ khóa nguy hiểm
            "|;" +                                     // Dấu chấm phẩy
            "|--.*" +                                  // Comment dòng
            "|/\\*.*\\*/" +                            // Comment khối
            "|'[^']*--[^']*'" +                        // Chuỗi chứa comment
            "|'[^']*;[^']*'" +                           // Chuỗi chứa chấm phẩy
            "",
            Pattern.DOTALL                             // Đảm bảo khớp cả xuống dòng
    );
    public static String sanitizeQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            return null;
        }

        String trimmedQuery = query.trim();

        if (DANGEROUS_PATTERN.matcher(trimmedQuery).find()) {
            throw new IllegalArgumentException("Query contains potentially dangerous SQL keywords or patterns");
        }

        if (!trimmedQuery.toUpperCase().startsWith("SELECT")) {
            throw new IllegalArgumentException("Only SELECT queries are allowed");
        }

        return trimmedQuery;
    }

    public static boolean isSafeQuery(String query) {
        try {
            sanitizeQuery(query);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}