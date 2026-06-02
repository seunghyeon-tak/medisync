package medisync.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.domain.user.entity.enums.BloodType;

@Entity
@Table(name = "patients")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Patient extends User {
    /**
     * 혈액형 (Rh+ A, Rh+ B, Rh+ AB, Rh+ O)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "blood_type", nullable = false)
    private BloodType bloodType;

    /**
     * 병력
     */
    @Column(name = "medical_history", length = 500)
    private String medicalHistory;

    /**
     * 복용중인 약
     */
    @Column(name = "current_medications", length = 500)
    private String currentMedications;
}
