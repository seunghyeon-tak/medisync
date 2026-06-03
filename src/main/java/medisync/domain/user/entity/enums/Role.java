package medisync.domain.user.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    PATIENT("환자"),
    DOCTOR("의사"),
    PHARMACIST("약사"),
    ;

    private final String description;
}
