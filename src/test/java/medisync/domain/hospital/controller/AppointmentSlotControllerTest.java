package medisync.domain.hospital.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import common.BaseControllerTest;
import medisync.domain.hospital.dto.AppointmentSlotCreateRequest;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.hospital.repository.HospitalRepository;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.DoctorRepository;
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

public class AppointmentSlotControllerTest extends BaseControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String RAW_PASSWORD = "testPassword1!";

    private Doctor saveDoctor(String email) {
        Hospital hospital = Hospital.builder()
                .name("테스트병원1")
                .address("서울시 강남구")
                .phone("02-3333-1111")
                .build();
        hospitalRepository.save(hospital);

        Doctor doctor = Doctor.builder()
                .name("의사테스터")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1989-01-01"))
                .role(Role.DOCTOR)
                .phone("010-2222-3333")
                .hospital(hospital)
                .licenseNumber("111111111")
                .build();

        return doctorRepository.save(doctor);
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
    void 슬롯_생성_성공() throws Exception {
        // given
        String email = "doctorUser1@test.com";
        saveDoctor(email);
        String accessToken = getAccessToken(email);
        AppointmentSlotCreateRequest request = AppointmentSlotCreateRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/appointment-slots")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    void 슬롯_생성_실패_시간_역전() throws Exception {
        // given
        String email = "doctorUser1@test.com";
        saveDoctor(email);
        String accessToken = getAccessToken(email);
        AppointmentSlotCreateRequest request = AppointmentSlotCreateRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("12:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/appointment-slots")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("H002"))
                .andExpect(jsonPath("$.message").value("시작 시간은 종료 시간보다 빨라야 합니다."));
    }

    @Test
    void 슬롯_생성_실패_시간_겹침() throws Exception {
        // given
        String email = "doctorUser1@test.com";
        Doctor doctor = saveDoctor(email);
        AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();
        appointmentSlotRepository.save(appointmentSlot);
        String accessToken = getAccessToken(email);
        AppointmentSlotCreateRequest request = AppointmentSlotCreateRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:30:00"))
                .endTime(LocalTime.parse("11:30:00"))
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/appointment-slots")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("H003"))
                .andExpect(jsonPath("$.message").value("이미 겹치는 진료 가능 시간이 존재합니다."));
    }

    @Test
    void 슬롯_생성_실패_인증_없음() throws Exception {
        // given
        String accessToken = "";
        AppointmentSlotCreateRequest request = AppointmentSlotCreateRequest.builder()
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/appointment-slots")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isForbidden());
    }
}
