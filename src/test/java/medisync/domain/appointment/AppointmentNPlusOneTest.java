package medisync.domain.appointment;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.entity.enums.CallType;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.hospital.repository.HospitalRepository;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class AppointmentNPlusOneTest {

    @Autowired
    AppointmentRepository appointmentRepository;
    @Autowired
    AppointmentSlotRepository appointmentSlotRepository;
    @Autowired
    PatientRepository patientRepository;
    @Autowired
    DoctorRepository doctorRepository;
    @Autowired
    HospitalRepository hospitalRepository;
    @Autowired
    EntityManager em;

    private Doctor doctor;

    private Patient patient1;
    private Patient patient2;
    private Patient patient3;

    private AppointmentSlot appointmentSlot1;
    private AppointmentSlot appointmentSlot2;
    private AppointmentSlot appointmentSlot3;

    @BeforeEach
    void setUp() {
        // 1. 병원, 의사 1명 저장 (동시성 테스트의 @BeforeEach 참고)
        // 병원 생성
        Hospital hospital = Hospital.builder()
                .name("테스터 병원")
                .address("서울시 광진구")
                .phone("02-1111-2222")
                .build();
        hospitalRepository.save(hospital);

        // 의사 생성
        doctor = Doctor.builder()
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
        // 2. 슬롯 3개, 환자 3명 저장 (이메일·전화번호는 서로 다르게)
        // 슬롯 생성
        appointmentSlot1 = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();
        appointmentSlot2 = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("12:00:00"))
                .endTime(LocalTime.parse("13:00:00"))
                .build();
        appointmentSlot3 = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("12:00:00"))
                .endTime(LocalTime.parse("13:00:00"))
                .build();

        appointmentSlotRepository.save(appointmentSlot1);
        appointmentSlotRepository.save(appointmentSlot2);
        appointmentSlotRepository.save(appointmentSlot3);

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
        patient3 = Patient.builder()
                .name("테스터환자3")
                .email("testerPa3@test.com")
                .password("1234qwer!2")
                .birthDay(LocalDate.parse("1990-02-01"))
                .address("서울시 성동구")
                .bloodType(BloodType.RHPlusA)
                .role(Role.PATIENT)
                .phone("010-1234-3333")
                .build();

        patientRepository.save(patient1);
        patientRepository.save(patient2);
        patientRepository.save(patient3);

        // 3. 환자 i + 슬롯 i 로 Appointment.create(...) 해서 예약 3건 저장
        Appointment appointment1 = Appointment.create(patient1, appointmentSlot1, "", "", CallType.VOICE);
        Appointment appointment2 = Appointment.create(patient2, appointmentSlot2, "", "", CallType.VOICE);
        Appointment appointment3 = Appointment.create(patient3, appointmentSlot3, "", "", CallType.VOICE);

        appointmentRepository.save(appointment1);
        appointmentRepository.save(appointment2);
        appointmentRepository.save(appointment3);

        em.flush();   // 지금까지 저장한 걸 DB에 반영
        em.clear();   // 영속성 컨텍스트 비우기 (함정 2 해결)
    }

    @Test
    void findBy_방식() {
        System.out.println("===== findBy 시작 =====");
        // findByDoctorId로 조회 → 반복문에서 getPatient().getName() 출력
        List<Appointment> byDoctorId = appointmentRepository.findByDoctorId(doctor.getId());
        for (Appointment a : byDoctorId) {
            System.out.println(a.getPatient().getName());
        }
    }

    @Test
    void joinFetch_방식() {
        System.out.println("===== join fetch 시작 =====");
        // findAllWithPatientByDoctorId로 조회 → 반복문에서 getPatient().getName() 출력
        List<Appointment> allWithPatientByDoctorId = appointmentRepository.findAllWithPatientByDoctorId(doctor.getId());
        for (Appointment a : allWithPatientByDoctorId) {
            System.out.println(a.getPatient().getName());
        }
    }
}
