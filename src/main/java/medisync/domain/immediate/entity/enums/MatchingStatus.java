package medisync.domain.immediate.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MatchingStatus {
    REQUESTED("신청 접수"),
    MATCHED("의사 배정"),
    CONFIRMED("의사 수락"),
    COMPLETE("진료 완료"),
    CANCEL("진료 취소"),
    FAILED("의사 매칭 실패"),
    ;

    private final String description;
}
