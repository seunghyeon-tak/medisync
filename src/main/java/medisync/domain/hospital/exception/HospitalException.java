package medisync.domain.hospital.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class HospitalException extends BaseException {
    private final HospitalErrorCode hospitalErrorCode;

    public HospitalException(HospitalErrorCode hospitalErrorCode) {
        super(hospitalErrorCode);
        this.hospitalErrorCode = hospitalErrorCode;
    }
}
