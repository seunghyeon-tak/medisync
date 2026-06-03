package medisync.domain.appointment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;
import medisync.domain.appointment.entity.enums.ReservationStatus;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;

/**
 * 진료 예약 엔티티
 * */
@Entity
@Table(name = "appointments")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Appointment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "slot_id", nullable = false)
    private AppointmentSlot appointmentSlot;

    /** 증상 */
    private String symptom;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", length = 20)
    @Builder.Default
    private ReservationStatus reservationStatus = ReservationStatus.WAITING;
}
