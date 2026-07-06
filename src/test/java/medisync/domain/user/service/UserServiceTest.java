package medisync.domain.user.service;

import common.BaseServiceTest;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.pharmacy.entity.Pharmacy;
import medisync.domain.user.dto.DoctorSignupRequest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.dto.PharmacistSignupRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.Pharmacist;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.mapper.UserMapper;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PharmacistRepository;
import medisync.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class UserServiceTest extends BaseServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PharmacistRepository pharmacistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private static final String TEST_PASSWORD = "testPassword1!";
    private static final String TEST_EMAIL_PATIENT = "pa_tester1@test.com";
    private static final String TEST_EMAIL_DOCTOR = "do_tester1@test.com";
    private static final String TEST_EMAIL_PHARMACIST = "ph_tester1@test.com";

    @Test
    void 이메일_중복시_예외발생() {
        // given
        String email = "tester1@test.com";
        when(userRepository.existsByEmail(email)).thenReturn(true);

        // when & then
        assertThrows(UserException.class, () -> userService.validateDuplicateEmail(email));
    }

    @Test
    void 이메일_정상_통과() {
        // given
        String email = "test1@test.com";

        // when
        when(userRepository.existsByEmail(email)).thenReturn(false);

        // then
        assertDoesNotThrow(() -> userService.validateDuplicateEmail(email));
    }

    @Test
    void 환자_저장_성공() {
        // given
        PatientSignupRequest request = PatientSignupRequest.builder()
                .name("환자테스터1")
                .email(TEST_EMAIL_PATIENT)
                .password(TEST_PASSWORD)
                .passwordConfirm(TEST_PASSWORD)
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구 111")
                .role(Role.PATIENT)
                .phone("010-1231-1231")
                .bloodType(BloodType.RHPlusA)
                .medicalHistory("")
                .currentMedications("")
                .build();

        Patient patient = Patient.builder()
                .name("환자테스터1")
                .email(TEST_EMAIL_PATIENT)
                .build();

        when(userMapper.patientMapper(any(), any())).thenReturn(patient);

        // when
        userService.savePatient(request);

        // then
        verify(userRepository).save(any(Patient.class));
    }

    @Test
    void 의사_저장_성공() {
        // given
        Hospital hospital = Hospital.builder()
                .name("서울병원")
                .address("서울시 강남구")
                .phone("02-1234-1234")
                .build();

        DoctorSignupRequest request = DoctorSignupRequest.builder()
                .name("의사테스터1")
                .email(TEST_EMAIL_DOCTOR)
                .password(TEST_PASSWORD)
                .passwordConfirm(TEST_PASSWORD)
                .birthDay(LocalDate.parse("1989-01-01"))
                .address("서울시 강남구 112")
                .role(Role.DOCTOR)
                .phone("010-1231-1232")
                .hospitalId(hospital.getId())
                .licenseNumber("1287181798")
                .build();

        Doctor doctor = Doctor.builder()
                .hospital(hospital)
                .licenseNumber("1287181798")
                .build();

        when(userMapper.doctorMapper(any(), any(), any())).thenReturn(doctor);

        // when
        userService.saveDoctor(request, hospital);

        // then
        verify(doctorRepository).save(any(Doctor.class));
    }

    @Test
    void 약사_저장_성공() {
        // given
        Pharmacy pharmacy = Pharmacy.builder()
                .name("서울약국")
                .address("서울시 서초구 123")
                .phone("02-0441-7812")
                .build();

        PharmacistSignupRequest request = PharmacistSignupRequest.builder()
                .name("약사테스터1")
                .email(TEST_EMAIL_PHARMACIST)
                .password(TEST_PASSWORD)
                .passwordConfirm(TEST_PASSWORD)
                .birthDay(LocalDate.parse("1989-01-01"))
                .address("서울시 서초구 423")
                .role(Role.PHARMACIST)
                .phone("02-1149-1198")
                .pharmacyId(pharmacy.getId())
                .licenseNumber("98187981798")
                .build();

        Pharmacist pharmacist = Pharmacist.builder()
                .pharmacy(pharmacy)
                .licenseNumber("98187981798")
                .build();

        when(userMapper.pharmacistMapper(any(), any(), any())).thenReturn(pharmacist);

        // when
        userService.savePharmacist(request, pharmacy);

        // then
        verify(pharmacistRepository).save(any(Pharmacist.class));
    }
}
