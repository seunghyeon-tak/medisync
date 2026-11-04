package medisync.domain.immediate.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;
import medisync.domain.user.entity.Doctor;

@Entity
@Table(name = "consult_rejections")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class ConsultRejection extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consult_id", nullable = false)
    private Consult consult;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    public static ConsultRejection create(Consult consult, Doctor doctor) {
        return ConsultRejection.builder()
                .consult(consult)
                .doctor(doctor)
                .build();
    }
}
