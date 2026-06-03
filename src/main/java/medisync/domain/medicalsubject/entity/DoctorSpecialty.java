package medisync.domain.medicalsubject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medisync.domain.user.entity.Doctor;

/**
 * 의사 전공 엔티티
 * */
@Entity
@Table(name = "doctor_specialties")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class DoctorSpecialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_subject_id", nullable = false)
    private MedicalSubject medicalSubject;
}
