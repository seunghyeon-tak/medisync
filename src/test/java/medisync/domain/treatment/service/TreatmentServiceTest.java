package medisync.domain.treatment.service;

import common.BaseServiceTest;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.entity.enums.ReservationStatus;
import medisync.domain.appointment.exception.AppointmentErrorCode;
import medisync.domain.appointment.exception.AppointmentException;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.medicalsubject.entity.MedicalSubject;
import medisync.domain.medicalsubject.exception.MedicalSubjectErrorCode;
import medisync.domain.medicalsubject.exception.MedicalSubjectException;
import medisync.domain.medicalsubject.repository.MedicalSubjectRepository;
import medisync.domain.treatment.dto.TreatmentCreateResponse;
import medisync.domain.treatment.entity.Treatment;
import medisync.domain.treatment.exception.TreatmentErrorCode;
import medisync.domain.treatment.exception.TreatmentException;
import medisync.domain.treatment.repository.TreatmentRepository;
import medisync.domain.user.entity.Doctor;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

public class TreatmentServiceTest extends BaseServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private MedicalSubjectRepository medicalSubjectRepository;

    @Mock
    private TreatmentRepository treatmentRepository;

    @InjectMocks
    private TreatmentService treatmentService;

    @Test
    void 정상_진료_완료_성공() {
        // given
        Long doctorId = 1L;
        Long appointmentId = 1L;
        Long medicalSubjectId = 1L;
        String content = "감기 증상으로 진료 완료";

        Doctor doctor = Doctor.builder().build();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .build();
        ReflectionTestUtils.setField(appointment, "id", appointmentId);

        MedicalSubject medicalSubject = MedicalSubject.builder().build();
        ReflectionTestUtils.setField(medicalSubject, "id", medicalSubjectId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(medicalSubjectRepository.findById(medicalSubjectId)).thenReturn(Optional.of(medicalSubject));
        when(treatmentRepository.existsByAppointmentId(appointmentId)).thenReturn(false);
        when(treatmentRepository.save(any(Treatment.class))).thenAnswer(invocation -> {
            Treatment savedTreatment = invocation.getArgument(0);
            ReflectionTestUtils.setField(savedTreatment, "id", 1L);
            return savedTreatment;
        });

        // when
        TreatmentCreateResponse response = treatmentService.createTreatment(doctorId, appointmentId, medicalSubjectId, content);

        // then
        assertEquals(1L, response.getId());
        assertEquals(ReservationStatus.COMPLETE, appointment.getReservationStatus());
    }

    @Test
    void 예약_없음() {
        // given
        Long doctorId = 1L;
        Long appointmentId = 1L;
        Long medicalSubjectId = 1L;
        String content = "";

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        // when
        AppointmentException exception = assertThrows(AppointmentException.class,
                () -> treatmentService.createTreatment(doctorId, appointmentId, medicalSubjectId, content));

        // then
        assertEquals(AppointmentErrorCode.APPOINTMENT_NOT_FOUND, exception.getAppointmentErrorCode());
    }

    @Test
    void 담당_의사_아님() {
        // given
        Long doctorId = 1L;
        Long otherDoctorId = 2L;
        Long appointmentId = 1L;
        Long medicalSubjectId = 1L;
        String content = "";

        Doctor doctor = Doctor.builder().build();
        ReflectionTestUtils.setField(doctor, "id", otherDoctorId);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .build();
        ReflectionTestUtils.setField(appointment, "id", appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        // when
        AppointmentException exception = assertThrows(AppointmentException.class,
                () -> treatmentService.createTreatment(doctorId, appointmentId, medicalSubjectId, content));

        // then
        assertEquals(AppointmentErrorCode.APPOINTMENT_DOCTOR_FORBIDDEN, exception.getAppointmentErrorCode());
    }

    @Test
    void 진료과목_없음() {
        // given
        Long doctorId = 1L;
        Long appointmentId = 1L;
        Long medicalSubjectId = 1L;
        String content = "";

        Doctor doctor = Doctor.builder().build();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .build();
        ReflectionTestUtils.setField(appointment, "id", appointmentId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(medicalSubjectRepository.findById(medicalSubjectId)).thenReturn(Optional.empty());

        // when
        MedicalSubjectException exception = assertThrows(MedicalSubjectException.class,
                () -> treatmentService.createTreatment(doctorId, appointmentId, medicalSubjectId, content));

        // then
        assertEquals(MedicalSubjectErrorCode.MEDICAL_SUBJECT_NOT_FOUND, exception.getMedicalSubjectErrorCode());
    }

    @Test
    void 이미_진료_완료됨() {
        // given
        Long doctorId = 1L;
        Long appointmentId = 1L;
        Long medicalSubjectId = 1L;
        String content = "";

        Doctor doctor = Doctor.builder().build();
        ReflectionTestUtils.setField(doctor, "id", doctorId);

        Appointment appointment = Appointment.builder()
                .doctor(doctor)
                .build();
        ReflectionTestUtils.setField(appointment, "id", appointmentId);

        MedicalSubject medicalSubject = MedicalSubject.builder().build();
        ReflectionTestUtils.setField(medicalSubject, "id", medicalSubjectId);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(medicalSubjectRepository.findById(medicalSubjectId)).thenReturn(Optional.of(medicalSubject));
        when(treatmentRepository.existsByAppointmentId(appointmentId)).thenReturn(true);

        // when
        TreatmentException exception = assertThrows(TreatmentException.class,
                () -> treatmentService.createTreatment(doctorId, appointmentId, medicalSubjectId, content));

        // then
        assertEquals(TreatmentErrorCode.ALREADY_TREATED, exception.getTreatmentErrorCode());
    }
}