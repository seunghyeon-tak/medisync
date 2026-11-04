package medisync.domain.immediate.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;
import medisync.domain.appointment.entity.enums.CallType;
import medisync.domain.immediate.entity.enums.MatchingStatus;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;

@Entity
@Table(name = "consults")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Consult extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = true)  // 매칭 전 까지 null
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", length = 20)
    @Builder.Default
    private CallType callType = CallType.VOICE;

    private String symptom;

    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(name = "matching_status", length = 20)
    @Builder.Default
    private MatchingStatus matchingStatus = MatchingStatus.REQUESTED;

    public static Consult create(Patient patient, String symptom, String picture, CallType callType) {
        return Consult.builder()
                .patient(patient)
                .symptom(symptom)
                .picture(picture)
                .callType(callType)
                .build();
    }

    // 의사 배정
    public void assignDoctor(Doctor doctor) {
        this.doctor = doctor;
        this.matchingStatus = MatchingStatus.MATCHED;
    }

    // 거절
    public void reject() {
        this.doctor = null;
        this.matchingStatus = MatchingStatus.REQUESTED;
    }

    // 확인
    public void confirm() {
        this.matchingStatus = MatchingStatus.CONFIRMED;
    }
}
