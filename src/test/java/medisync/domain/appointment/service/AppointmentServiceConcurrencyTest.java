package medisync.domain.appointment.service;

import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.hospital.repository.HospitalRepository;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PatientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public class AppointmentServiceConcurrencyTest {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private AppointmentService appointmentService;

    private Patient patient1;
    private Patient patient2;
    private AppointmentSlot appointmentSlot;

    @BeforeEach
    void beforeEach() {
        // 병원 생성
        Hospital hospital = Hospital.builder()
                .name("테스터 병원")
                .address("서울시 광진구")
                .phone("02-1111-2222")
                .build();
        hospitalRepository.save(hospital);

        // 의사 생성
        Doctor doctor = Doctor.builder()
                .name("테스터의사1")
                .email("testerDoctor1@test.com")
                .password("1234qwer!1")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.DOCTOR)
                .phone("010-1234-1234")
                .hospital(hospital)
                .licenseNumber("1111113341")
                .build();
        doctorRepository.save(doctor);

        // 슬롯 생성
        appointmentSlot = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();
        appointmentSlotRepository.save(appointmentSlot);

        // 환자 생성 (2명)
        patient1 = Patient.builder()
                .name("테스터환자1")
                .email("testerPa1@test.com")
                .password("1234qwer!1")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-1111")
                .bloodType(BloodType.RHPlusA)
                .build();

        patient2 = Patient.builder()
                .name("테스터환자2")
                .email("testerPa2@test.com")
                .password("1234qwer!2")
                .birthDay(LocalDate.parse("1990-02-01"))
                .address("서울시 성동구")
                .bloodType(BloodType.RHPlusA)
                .role(Role.PATIENT)
                .phone("010-1234-2222")
                .build();
        patientRepository.save(patient1);
        patientRepository.save(patient2);

    }

    @AfterEach
    void afterEach() {
        appointmentRepository.deleteAll();
        patientRepository.deleteAll();
        appointmentSlotRepository.deleteAll();
        doctorRepository.deleteAll();
        hospitalRepository.deleteAll();
    }

    @Test
    void 동시_예약_요청시_선착순_성공() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        CountDownLatch latch = new CountDownLatch(2);

        executorService.submit(() -> {
            try {
                appointmentService.createAppointment(appointmentSlot.getId(), patient1.getId(), "", "");
                successCount.incrementAndGet();
            } catch (HospitalException e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        executorService.submit(() -> {
            try {
                appointmentService.createAppointment(appointmentSlot.getId(), patient2.getId(), "", "");
                successCount.incrementAndGet();
            } catch (HospitalException e) {
                failCount.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });

        latch.await();

        assertEquals(1, successCount.get());
        assertEquals(1, failCount.get());
        assertEquals(1, appointmentRepository.findAll().size());
        AppointmentSlot updatedSlot = appointmentSlotRepository.findById(appointmentSlot.getId())
                .orElseThrow();
        assertFalse(updatedSlot.isAvailable());

        executorService.shutdown();

    }
}
