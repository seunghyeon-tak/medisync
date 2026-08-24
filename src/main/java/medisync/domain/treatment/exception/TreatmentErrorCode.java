package medisync.domain.treatment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum TreatmentErrorCode implements ErrorCode {
    TREATMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "T001", "찾는 진료 내역이 없습니다."),
    ALREADY_TREATED(HttpStatus.BAD_REQUEST, "T002", "이미 진료가 존재합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
