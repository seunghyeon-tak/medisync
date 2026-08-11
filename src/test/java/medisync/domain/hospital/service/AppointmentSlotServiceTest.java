package medisync.domain.hospital.service;

import common.BaseServiceTest;
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
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AppointmentSlotServiceTest extends BaseServiceTest {
    @Mock
    private AppointmentSlotRepository appointmentSlotRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private AppointmentSlotMapper appointmentSlotMapper;

    @InjectMocks
    private AppointmentSlotService appointmentSlotService;

    @Test
    void 슬롯_생성_성공() {
        // given
        Long doctorId = 1L;
        Long appointmentId = 1L;
        LocalDate date = LocalDate.parse("2026-01-01");
        LocalTime startTime = LocalTime.parse("12:00:00");
        LocalTime endTime = LocalTime.parse("13:00:00");
        Doctor doctor = Doctor.builder().build();
        AppointmentSlot appointmentSlot = AppointmentSlot.builder().build();
        ReflectionTestUtils.setField(appointmentSlot, "id", appointmentId);

        when(appointmentSlotRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(List.of());
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(appointmentSlotMapper.appointmentSlotMapper(doctor, date, startTime, endTime)).thenReturn(appointmentSlot);

        // when
        AppointmentSlotCreateResponse response = appointmentSlotService.createSlot(doctorId, date, startTime, endTime);

        // then
        assertEquals(appointmentId, response.getId());
        verify(appointmentSlotRepository).save(any(AppointmentSlot.class));
    }

    @Test
    void 슬롯_생성_실패_시간_역전() {
        // given
        Long doctorId = 1L;
        LocalDate date = LocalDate.parse("2026-01-01");
        LocalTime startTime = LocalTime.parse("13:00:00");
        LocalTime endTime = LocalTime.parse("12:00:00");

        // when
        HospitalException exception = assertThrows(HospitalException.class,
                () -> appointmentSlotService.createSlot(doctorId, date, startTime, endTime));

        // then
        assertEquals(HospitalErrorCode.INVALID_TIME_RANGE, exception.getHospitalErrorCode());
    }

    @Test
    void 슬롯_생성_실패_시간_겹침() {
        // given
        Long doctorId = 1L;
        LocalDate date = LocalDate.parse("2026-01-01");
        LocalTime startTime = LocalTime.parse("10:30:00");
        LocalTime endTime = LocalTime.parse("11:30:00");
        Doctor doctor = Doctor.builder().build();
        ReflectionTestUtils.setField(doctor, "id", doctorId);
        AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.parse("2026-01-01"))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();

        when(appointmentSlotRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(List.of(appointmentSlot));

        // when
        HospitalException exception = assertThrows(HospitalException.class,
                () -> appointmentSlotService.createSlot(doctorId, date, startTime, endTime));

        // then
        assertEquals(HospitalErrorCode.OVERLAPPING_APPOINTMENT_SLOT, exception.getHospitalErrorCode());
    }

    @Test
    void 슬롯_생성_실패_의사_없음() {
        // given
        Long doctorId = 1L;
        LocalDate date = LocalDate.parse("2026-01-01");
        LocalTime startTime = LocalTime.parse("10:30:00");
        LocalTime endTime = LocalTime.parse("11:30:00");

        when(appointmentSlotRepository.findByDoctorIdAndDate(doctorId, date)).thenReturn(List.of());
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        // when
        UserException exception = assertThrows(UserException.class,
                () -> appointmentSlotService.createSlot(doctorId, date, startTime, endTime));

        // then
        assertEquals(UserErrorCode.USER_NOT_FOUND, exception.getUserErrorCode());

    }

}
