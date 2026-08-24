package medisync.domain.appointment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import common.BaseControllerTest;
import medisync.domain.appointment.dto.AppointmentCreateRequest;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.entity.enums.CallType;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.hospital.repository.HospitalRepository;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppointmentControllerTest extends BaseControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String RAW_PASSWORD = "testPassword1!";

    private Doctor saveDoctor() {
        // 병원 생성
        Hospital hospital = Hospital.builder()
                .name("테스트병원1")
                .address("서울시 강남구")
                .phone("02-3333-1111")
                .build();
        hospitalRepository.save(hospital);

        // 의사 생성
        Doctor doctor = Doctor.builder()
                .name("의사테스터")
                .email("testDoctor1@test.com")
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1989-01-01"))
                .role(Role.DOCTOR)
                .phone("010-2222-3333")
                .hospital(hospital)
                .licenseNumber("111111111")
                .build();
        return doctorRepository.save(doctor);
    }

    private AppointmentSlot saveSlot() {
        Doctor doctor = saveDoctor();

        // 슬롯 생성
        AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();
        return appointmentSlotRepository.save(appointmentSlot);
    }

    private Patient savePatient(String email) {
        // 환자 생성
        Patient patient = Patient.builder()
                .name("테스터환자1")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-1111")
                .bloodType(BloodType.RHPlusA)
                .build();
        return patientRepository.save(patient);
    }

    private Appointment saveAppointment(String email) {
        Patient patient = savePatient(email);
        AppointmentSlot appointmentSlot = saveSlot();
        appointmentSlot.markAsBooked();

        // 진료 예약 생성
        Appointment appointment = Appointment.create(patient, appointmentSlot, "", "", CallType.VOICE);

        return appointmentRepository.save(appointment);
    }

    private String getAccessToken(String email) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(RAW_PASSWORD)
                .build();

        String responseBody = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse().getContentAsString();

        return JsonPath.read(responseBody, "$.data.accessToken");
    }

    @Test
    void 예약_성공() throws Exception {
        // given
        String email = "testerPa1@test.com";
        savePatient(email);
        String accessToken = getAccessToken(email);
        AppointmentSlot slot = saveSlot();

        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .slotId(slot.getId())
                .callType(CallType.VOICE)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    void 예약_실패_슬롯_없음() throws Exception {
        // given
        String email = "testerPa1@test.com";
        savePatient(email);
        String accessToken = getAccessToken(email);

        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .slotId(99999L)
                .callType(CallType.VOICE)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("H004"))
                .andExpect(jsonPath("$.message").value("해당하는 진료 예약 시간을 찾을 수 없습니다."));
    }

    @Test
    void 예약_실패_이미_예약된_슬롯() throws Exception {
        // given
        String email = "testerPa1@test.com";
        Appointment appointment = saveAppointment(email);
        String accessToken = getAccessToken(email);

        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .slotId(appointment.getAppointmentSlot().getId())
                .callType(CallType.VOICE)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("H005"))
                .andExpect(jsonPath("$.message").value("이미 예약이 마감된 진료 시간입니다."));
    }

    @Test
    void 예약_실패_인증_없음() throws Exception {
        // given
        String accessToken = "";

        AppointmentCreateRequest request = AppointmentCreateRequest.builder()
                .slotId(9999L)
                .callType(CallType.VOICE)
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/appointments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isForbidden());

    }
}
