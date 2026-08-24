package medisync.domain.medicalsubject.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MedicalSubjectErrorCode implements ErrorCode {
    MEDICAL_SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "M001", "찾는 진료과목이 없습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
