package medisync.domain.treatment.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class TreatmentException extends BaseException {
    private final TreatmentErrorCode treatmentErrorCode;

    public TreatmentException(TreatmentErrorCode treatmentErrorCode) {
        super(treatmentErrorCode);
        this.treatmentErrorCode = treatmentErrorCode;
    }
}
