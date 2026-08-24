package medisync.domain.medicalsubject.repository;

import medisync.domain.medicalsubject.entity.MedicalSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicalSubjectRepository extends JpaRepository<MedicalSubject, Long> {
}
