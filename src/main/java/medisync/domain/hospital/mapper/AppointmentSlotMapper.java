package medisync.domain.hospital.mapper;

import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.user.entity.Doctor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class AppointmentSlotMapper {
    public AppointmentSlot appointmentSlotMapper(Doctor doctor, LocalDate date, LocalTime startTime, LocalTime endTime) {
        return AppointmentSlot.builder()
                .doctor(doctor)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .build();

    }
}
