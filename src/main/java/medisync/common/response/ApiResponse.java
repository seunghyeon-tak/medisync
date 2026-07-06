package medisync.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final boolean status;
    private final T data;
    private final String code;
    private final String message;

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse<>(true, null, null, null);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, code, message);
    }
}
