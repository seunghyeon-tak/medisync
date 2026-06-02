package medisync.domain.pharmacy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;

/**
 * 약국 엔티티
 */
@Entity
@Table(name = "pharmacies")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Pharmacy extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 50, nullable = false)
    private String address;

    @Column(length = 20, nullable = false)
    private String phone;

    private String content;
}
