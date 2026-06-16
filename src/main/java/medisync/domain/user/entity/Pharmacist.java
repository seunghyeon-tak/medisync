package medisync.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.domain.pharmacy.entity.Pharmacy;

@Entity
@Table(name = "pharmacists")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Pharmacist extends User {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pharmacy_id", nullable = false)
    private Pharmacy pharmacy;

    @Column(name = "license_number", length = 50, nullable = false)
    private String licenseNumber;
}
