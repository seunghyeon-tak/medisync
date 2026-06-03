package medisync.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.common.entity.BaseEntity;
import medisync.domain.user.entity.enums.Role;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@SuperBuilder
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 20, nullable = false)
    private String name;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 500)
    private String password;

    @Column(name = "birth_day", nullable = false)
    private LocalDate birthDay;

    @Column(length = 100)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Role role;

    @Column(length = 30, nullable = false)
    private String phone;

    @Column(name = "provider_id", length = 500)
    private String providerId;

    @Column(length = 500)
    private String provider;
}
