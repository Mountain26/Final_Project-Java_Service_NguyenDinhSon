package ra.edu.finalproject_javaservice.common;

import java.util.List;

public record ApiResponse<T>(boolean success, String message, T data, List<String> errors) {
    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> fail(String message, List<String> errors) {
        return new ApiResponse<>(false, message, null, errors);
    }
}
