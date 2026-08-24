package medisync.domain.appointment.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AppointmentErrorCode implements ErrorCode {
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "예약된 내용이 없습니다."),
    APPOINTMENT_DOCTOR_FORBIDDEN(HttpStatus.FORBIDDEN, "A002", "예약한 진료의 담당 의사가 아닙니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
