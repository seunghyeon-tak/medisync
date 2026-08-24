package medisync.domain.appointment.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.appointment.dto.AppointmentCreateRequest;
import medisync.domain.appointment.dto.AppointmentCreateResponse;
import medisync.domain.appointment.service.AppointmentService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentFacade {
    private final AppointmentService appointmentService;

    public AppointmentCreateResponse createAppointment(Long patientId, AppointmentCreateRequest request) {
        return appointmentService.createAppointment(
                request.getSlotId(), patientId, request.getSymptom(), request.getPicture(), request.getCallType()
        );
    }
}
