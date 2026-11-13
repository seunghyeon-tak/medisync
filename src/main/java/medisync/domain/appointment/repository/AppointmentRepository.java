package medisync.domain.appointment.repository;

import medisync.domain.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorId(Long doctorId);

    @Query("select a from Appointment a join a.patient p where a.doctor.id = :doctorId")
    List<Appointment> findAllJoinByDoctorId(Long doctorId);

    @Query("select a from Appointment a join fetch a.patient where a.doctor.id = :doctorId")
    List<Appointment> findAllWithPatientByDoctorId(Long doctorId);
}
