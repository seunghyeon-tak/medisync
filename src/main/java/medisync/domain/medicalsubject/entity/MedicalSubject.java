package medisync.domain.medicalsubject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;

/**
 * 전공 엔티티
 * */
@Entity
@Table(name = "medical_subjects")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class MedicalSubject extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;
}
