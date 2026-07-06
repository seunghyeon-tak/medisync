package medisync.domain.pharmacy.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class PharmacyException extends BaseException {
    private final PharmacyErrorCode pharmacyErrorCode;

    public PharmacyException(PharmacyErrorCode pharmacyErrorCode) {
        super(pharmacyErrorCode);
        this.pharmacyErrorCode = pharmacyErrorCode;
    }
}
