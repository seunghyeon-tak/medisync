package medisync.domain.hospital.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.hospital.dto.AppointmentSlotCreateRequest;
import medisync.domain.hospital.dto.AppointmentSlotCreateResponse;
import medisync.domain.hospital.service.AppointmentSlotService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppointmentSlotFacade {
    private final AppointmentSlotService appointmentSlotService;

    public AppointmentSlotCreateResponse createSlot(Long doctorId, AppointmentSlotCreateRequest request) {
        return appointmentSlotService.createSlot(doctorId, request.getDate(), request.getStartTime(), request.getEndTime());
    }
}
