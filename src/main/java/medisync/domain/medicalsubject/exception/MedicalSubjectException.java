package medisync.domain.medicalsubject.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class MedicalSubjectException extends BaseException {
    private final MedicalSubjectErrorCode medicalSubjectErrorCode;

    public MedicalSubjectException(MedicalSubjectErrorCode medicalSubjectErrorCode) {
        super(medicalSubjectErrorCode);
        this.medicalSubjectErrorCode = medicalSubjectErrorCode;
    }
}
