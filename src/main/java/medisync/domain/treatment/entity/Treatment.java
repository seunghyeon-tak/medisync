package medisync.domain.treatment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.medicalsubject.entity.MedicalSubject;

/**
 * 진료 내역 엔티티
 * */
@Entity
@Table(name = "treatments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Treatment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_subject_id", nullable = false)
    private MedicalSubject medicalSubject;

    private String content;
}
