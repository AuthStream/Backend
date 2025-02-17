package authstream.utils;

import java.lang.reflect.Field;

public class Util {
    public static <T> T updateNonNullFields(T target, T source) {
        if (source == null || target == null) return null;

        for (Field field : source.getClass().getDeclaredFields()) {
            field.setAccessible(true); // Cho phép truy cập cả private fields
            try {
                Object value = field.get(source);
                if (value != null) {
                    field.set(target, value); // Gán giá trị vào target
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return target;
    }
}
