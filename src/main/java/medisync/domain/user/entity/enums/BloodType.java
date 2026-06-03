package medisync.domain.user.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BloodType {
    RHPlusA("RH+ A"),
    RHPlusB("RH+ B"),
    RHPlusAB("RH+ AB"),
    RHPlusO("RH+ O"),
    RHMinusA("RH- A"),
    RHMinusB("RH- B"),
    RHMinusAB("RH- AB"),
    RHMinusO("RH- O"),
    UNKNOWN("모름"),
    ;

    private final String description;
}
