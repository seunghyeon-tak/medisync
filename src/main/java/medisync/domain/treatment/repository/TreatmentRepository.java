package medisync.domain.treatment.repository;

import medisync.domain.treatment.entity.Treatment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreatmentRepository extends JpaRepository<Treatment, Long> {
    boolean existsByAppointmentId(Long appointmentId);
}
