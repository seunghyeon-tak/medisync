package medisync.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import common.BaseControllerTest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원가입_성공() throws Exception {
        // given
        PatientSignupRequest request = PatientSignupRequest.builder()
                .name("환자테스터1")
                .email("patient1@test.com")
                .password("testPassword1!")
                .passwordConfirm("testPassword1!")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .bloodType(BloodType.RHPlusA)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/users/signup/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
        assertTrue(userRepository.existsByEmail("patient1@test.com"));
    }

    @Test
    void 이메일_중복_실패() throws Exception {
        // given
        Patient patient = Patient.builder()
                .name("환자테스터1")
                .email("patient1@test.com")
                .password("testPassword1!")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .bloodType(BloodType.RHPlusA)
                .build();
        userRepository.save(patient);

        PatientSignupRequest request = PatientSignupRequest.builder()
                .name("환자테스터1")
                .email("patient1@test.com")
                .password("testPassword1!")
                .passwordConfirm("testPassword1!")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .bloodType(BloodType.RHPlusA)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/users/signup/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U001"))
                .andExpect(jsonPath("$.message").value("중복된 이메일 입니다."));
    }

    @Test
    void 비밀번호_불일치_실패() throws Exception {
        // given
        PatientSignupRequest request = PatientSignupRequest.builder()
                .name("환자테스터2")
                .email("patient2@test.com")
                .password("testPassword1!")
                .passwordConfirm("testPassword2!")
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .bloodType(BloodType.RHPlusA)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/users/signup/patient")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U002"))
                .andExpect(jsonPath("$.message").value("비밀번호가 일치하지 않습니다."));
    }
}
