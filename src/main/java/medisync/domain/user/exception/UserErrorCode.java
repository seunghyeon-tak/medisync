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
    UNABLE_LOGIN(HttpStatus.BAD_REQUEST, "U003", "이메일, 비밀번호가 올바르지 않습니다."),
    TOKEN_HAS_EXPIRED(HttpStatus.UNAUTHORIZED, "U004", "토큰이 만료되었습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "U005", "토큰이 유효하지 않습니다. 다시 로그인해주세요."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U006", "사용자를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
