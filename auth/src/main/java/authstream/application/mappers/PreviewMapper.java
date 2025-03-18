package authstream.application.mappers;

import org.apache.commons.lang3.tuple.Pair;

import authstream.application.dtos.PreviewSQLResponseDto;

public class PreviewMapper {

    public static PreviewSQLResponseDto toDto(Pair<String, String> result) {
        if (result == null) {
            return null;
        }
        return new PreviewSQLResponseDto(result.getLeft(), result.getRight());
    }
}