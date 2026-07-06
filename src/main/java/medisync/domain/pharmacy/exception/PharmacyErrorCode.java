package medisync.domain.pharmacy.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum PharmacyErrorCode implements ErrorCode {
    PHARMACY_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "해당하는 약국을 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
