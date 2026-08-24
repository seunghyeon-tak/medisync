package medisync.domain.appointment.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.appointment.dto.AppointmentCreateResponse;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.entity.enums.CallType;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.exception.HospitalErrorCode;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;

    @Transactional
    public AppointmentCreateResponse createAppointment(Long slotId, Long patientId, String symptom, String picture, CallType callType) {
        AppointmentSlot slot = appointmentSlotRepository.findByIdForUpdate(slotId)
                .orElseThrow(() -> new HospitalException(HospitalErrorCode.APPOINTMENT_SLOT_NOT_FOUND));

        if (!slot.isAvailable()) {
            throw new HospitalException(HospitalErrorCode.APPOINTMENT_SLOT_ALREADY_BOOKED);
        }

        Patient patient = patientRepository.findById(patientId).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Appointment appointment = Appointment.create(patient, slot, symptom, picture, callType);

        appointmentRepository.save(appointment);
        slot.markAsBooked();

        return AppointmentCreateResponse.builder()
                .id(appointment.getId())
                .build();
    }
}
