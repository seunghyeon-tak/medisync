package medisync.common.exception;

import lombok.Getter;
import medisync.common.response.ErrorCode;

@Getter
public class BaseException extends RuntimeException {
    private ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
