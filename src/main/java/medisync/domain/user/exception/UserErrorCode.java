package medisync.domain.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "U001", "중복된 이메일 입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "U002", "비밀번호가 일치하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
