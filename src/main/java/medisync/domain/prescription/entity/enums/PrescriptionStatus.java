package medisync.domain.prescription.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PrescriptionStatus {
    WAITING("처방 대기"),
    COMPLETE("제조 완료"),
    DELIVER("배송 중"),
    DELIVER_COMPLETE("배송 완료"),
    ;
    private final String description;
}
