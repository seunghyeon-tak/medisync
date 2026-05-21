package spring_boot_starter_kit.common.exception;

import lombok.Getter;
import spring_boot_starter_kit.common.response.ErrorCode;

@Getter
public class BaseException extends RuntimeException {
    private ErrorCode errorCode;

    public BaseException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }

}
