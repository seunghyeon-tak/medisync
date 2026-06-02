package medisync.domain.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;

@Entity
@Table(name = "hospitals")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Hospital extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(length = 50, nullable = false)
    private String phone;

    private String content;

}
