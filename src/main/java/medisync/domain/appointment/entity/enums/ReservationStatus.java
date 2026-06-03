package medisync.domain.appointment.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    WAITING("예약 대기"),
    CONFIRMED("예약 확정"),
    CANCEL("예약 취소"),
    COMPLETE("진료 완료"),
    ;

    private final String description;
}
