package medisync.domain.hospital.repository;

import medisync.domain.hospital.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
    List<AppointmentSlot> findByDoctorIdAndDate(Long doctorId, LocalDate date);
}
