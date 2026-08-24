package medisync.domain.treatment.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.appointment.entity.Appointment;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TreatmentService {
    private final AppointmentRepository appointmentRepository;
    private final MedicalSubjectRepository medicalSubjectRepository;
    private final TreatmentRepository treatmentRepository;

    @Transactional
    public TreatmentCreateResponse createTreatment(Long doctorId, Long appointmentId, Long medicalSubjectId, String content) {
        // 찾는 예약이 없을때
        Appointment appointment = appointmentRepository.findById(appointmentId).orElseThrow(
                () -> new AppointmentException(AppointmentErrorCode.APPOINTMENT_NOT_FOUND)
        );

        // 진료 예약된 의사와 현재 진료보는 의사가 다를경우
        if (!appointment.getDoctor().getId().equals(doctorId)) {
            throw new AppointmentException(AppointmentErrorCode.APPOINTMENT_DOCTOR_FORBIDDEN);
        }

        // 찾는 진료 과목이 없을때
        MedicalSubject medicalSubject = medicalSubjectRepository.findById(medicalSubjectId).orElseThrow(
                () -> new MedicalSubjectException(MedicalSubjectErrorCode.MEDICAL_SUBJECT_NOT_FOUND)
        );

        // 이미 해당 예약에 대한 진료가 존재하는 경우
        if (treatmentRepository.existsByAppointmentId(appointmentId)) {
            throw new TreatmentException(TreatmentErrorCode.ALREADY_TREATED);
        }

        // 진료 생성
        Treatment treatment = Treatment.create(appointment, medicalSubject, content);
        treatmentRepository.save(treatment);

        // 진료 완료 상태 변경
        appointment.reservationStatusComplete();

        return TreatmentCreateResponse.builder()
                .id(treatment.getId())
                .build();
    }
}
