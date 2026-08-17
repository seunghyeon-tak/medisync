package medisync.domain.appointment.service;

import common.BaseServiceTest;
import medisync.domain.appointment.dto.AppointmentCreateResponse;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.exception.HospitalErrorCode;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class AppointmentServiceTest extends BaseServiceTest {
    @Mock
    private AppointmentSlotRepository appointmentSlotRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void 정상_예약_성공() {
        // given
        Long slotId = 1L;
        Long patientId = 1L;
        String symptom = "";
        String picture = "";
        AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                .build();
        Patient patient = Patient.builder().build();
        ReflectionTestUtils.setField(appointmentSlot, "id", slotId);
        ReflectionTestUtils.setField(patient, "id", patientId);

        when(appointmentSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(appointmentSlot));
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> {
            Appointment savedAppointment = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedAppointment, "id", 1L);
            return savedAppointment;
        });

        // when
        AppointmentCreateResponse response = appointmentService.createAppointment(slotId, patientId, symptom, picture);

        // then
        assertEquals(1, response.getId());
    }

    @Test
    void 슬롯_없음() {
        // given
        Long slotId = 1L;
        Long patientId = 1L;
        String symptom = "";
        String picture = "";

        when(appointmentSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.empty());

        // when
        HospitalException exception = assertThrows(HospitalException.class,
                () -> appointmentService.createAppointment(slotId, patientId, symptom, picture));

        // then
        assertEquals(HospitalErrorCode.APPOINTMENT_SLOT_NOT_FOUND, exception.getHospitalErrorCode());
    }

    @Test
    void 이미_예약된_슬롯() {
        // given
        Long slotId = 1L;
        Long patientId = 1L;
        String symptom = "";
        String picture = "";
        AppointmentSlot appointmentSlot = AppointmentSlot.builder().isAvailable(false).build();
        ReflectionTestUtils.setField(appointmentSlot, "id", 1L);

        when(appointmentSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(appointmentSlot));

        // when
        HospitalException exception = assertThrows(HospitalException.class,
                () -> appointmentService.createAppointment(slotId, patientId, symptom, picture));

        // then
        assertEquals(HospitalErrorCode.APPOINTMENT_SLOT_ALREADY_BOOKED, exception.getHospitalErrorCode());
    }

    @Test
    void 환자_없음() {
        // given
        Long slotId = 1L;
        Long patientId = 1L;
        String symptom = "";
        String picture = "";
        AppointmentSlot appointmentSlot = AppointmentSlot.builder().build();
        ReflectionTestUtils.setField(appointmentSlot, "id", 1L);

        when(appointmentSlotRepository.findByIdForUpdate(slotId)).thenReturn(Optional.of(appointmentSlot));
        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        // when
        UserException exception = assertThrows(UserException.class,
                () -> appointmentService.createAppointment(slotId, patientId, symptom, picture));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getUserErrorCode());
    }
}
