package medisync.domain.appointment.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallType {
    VOICE("음성 통화"),
    VIDEO("영상 통화"),
    ;

    private final String description;
}
