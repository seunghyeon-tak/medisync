package medisync.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "pharmacists")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class Pharmacist extends User {
    @Column(name = "license_number", length = 50, nullable = false)
    private String licenseNumber;
}
