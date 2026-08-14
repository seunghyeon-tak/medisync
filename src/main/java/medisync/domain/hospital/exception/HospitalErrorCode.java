package medisync.domain.hospital.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ErrorCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum HospitalErrorCode implements ErrorCode {
    HOSPITAL_NOT_FOUND(HttpStatus.NOT_FOUND, "H001", "해당하는 병원을 찾을 수 없습니다."),
    INVALID_TIME_RANGE(HttpStatus.BAD_REQUEST, "H002", "시작 시간은 종료 시간보다 빨라야 합니다."),
    OVERLAPPING_APPOINTMENT_SLOT(HttpStatus.BAD_REQUEST, "H003", "이미 겹치는 진료 가능 시간이 존재합니다."),
    APPOINTMENT_SLOT_NOT_FOUND(HttpStatus.NOT_FOUND, "H004", "해당하는 진료 예약 시간을 찾을 수 없습니다."),
    APPOINTMENT_SLOT_ALREADY_BOOKED(HttpStatus.BAD_REQUEST, "H005", "이미 예약이 마감된 진료 시간입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
