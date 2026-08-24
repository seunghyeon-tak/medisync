package medisync.domain.appointment.exception;

import lombok.Getter;
import medisync.common.exception.BaseException;

@Getter
public class AppointmentException extends BaseException {
    private final AppointmentErrorCode appointmentErrorCode;

    public AppointmentException(AppointmentErrorCode appointmentErrorCode) {
        super(appointmentErrorCode);
        this.appointmentErrorCode = appointmentErrorCode;
    }

}
