package medisync.domain.hospital.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HospitalErrorCode implements ErrorCode {
    HOSPITAL_NOT_FOUND(HttpStatus.NOT_FOUND, "H001", "해당하는 병원 정보를 찾을 수 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
