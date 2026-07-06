package medisync.domain.user.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class UserException extends BaseException {
    private final UserErrorCode userErrorCode;

    public UserException(UserErrorCode userErrorCode) {
        super(userErrorCode);
        this.userErrorCode = userErrorCode;
    }
}
