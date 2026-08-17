package medisync.domain.hospital.repository;

import jakarta.persistence.LockModeType;
import medisync.domain.hospital.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AppointmentSlot a where a.id = :id")
    Optional<AppointmentSlot> findByIdForUpdate(Long id);
}
