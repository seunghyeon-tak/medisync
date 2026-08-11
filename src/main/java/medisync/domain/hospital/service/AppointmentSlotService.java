package medisync.domain.hospital.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.hospital.dto.AppointmentSlotCreateResponse;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.exception.HospitalErrorCode;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.mapper.AppointmentSlotMapper;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentSlotService {
    private final AppointmentSlotRepository appointmentSlotRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentSlotMapper appointmentSlotMapper;

    @Transactional
    public AppointmentSlotCreateResponse createSlot(Long doctorId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        // startTime < endTime 검증
        if (!startTime.isBefore(endTime)) {
            throw new HospitalException(HospitalErrorCode.INVALID_TIME_RANGE);
        }

        // AppointmentSlotRepository.findByDoctorIdAndDate(doctorId, date)
        List<AppointmentSlot> appointmentSlots = appointmentSlotRepository.findByDoctorIdAndDate(doctorId, date);

        // 겹치는 슬롯 있는지 확인 (겹치면 예외)
        for (AppointmentSlot slot : appointmentSlots) {
            if (slot.getStartTime().isBefore(endTime) && startTime.isBefore(slot.getEndTime())) {
                throw new HospitalException(HospitalErrorCode.OVERLAPPING_APPOINTMENT_SLOT);
            }
        }

        // AppointmentSlot 엔티티 만들어서 저장
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new UserException(UserErrorCode.USER_NOT_FOUND));
        AppointmentSlot appointmentSlot = appointmentSlotMapper.appointmentSlotMapper(doctor, date, startTime, endTime);
        appointmentSlotRepository.save(appointmentSlot);

        return AppointmentSlotCreateResponse.builder()
                .id(appointmentSlot.getId())
                .build();
    }
}
