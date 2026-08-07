package medisync.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import common.BaseControllerTest;
import medisync.common.jwt.JwtProvider;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String RAW_PASSWORD = "testPassword1!";

    private Patient savePatient(String email) {
        Patient patient = Patient.builder()
                .name("환자테스터")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-5678")
                .bloodType(BloodType.RHPlusA)
                .build();
        return userRepository.save(patient);
    }

    @Test
    void 로그인_성공() throws Exception {
        // given
        savePatient("login1@test.com");
        LoginRequest request = LoginRequest.builder()
                .email("login1@test.com")
                .password(RAW_PASSWORD)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("PATIENT"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void 로그인_실패_존재하지_않는_이메일() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("notfound@test.com")
                .password(RAW_PASSWORD)
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U003"))
                .andExpect(jsonPath("$.message").value("이메일, 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 로그인_실패_비밀번호_불일치() throws Exception {
        // given
        savePatient("login2@test.com");
        LoginRequest request = LoginRequest.builder()
                .email("login2@test.com")
                .password("wrongPassword1!")
                .build();

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("U003"))
                .andExpect(jsonPath("$.message").value("이메일, 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 로그아웃_성공() throws Exception {
        // given
        Patient patient = savePatient("logout1@test.com");
        String loginResponseBody = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        LoginRequest.builder()
                                                .email("logout1@test.com")
                                                .password(RAW_PASSWORD)
                                                .build()
                                ))
                )
                .andReturn().getResponse().getContentAsString();
        String accessToken = JsonPath.read(loginResponseBody, "$.data.accessToken");

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/logout")
                        .header("Authorization", "Bearer " + accessToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
        assertNotNull(redisTemplate.opsForValue().get("blacklist:" + accessToken));
        assertNull(redisTemplate.opsForValue().get("refresh:" + patient.getId()));
    }

    @Test
    void 토큰_재발급_성공() throws Exception {
        // given
        savePatient("reissue1@test.com");
        String loginResponseBody = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                        LoginRequest.builder()
                                                .email("reissue1@test.com")
                                                .password(RAW_PASSWORD)
                                                .build()
                                ))
                )
                .andReturn().getResponse().getContentAsString();
        String refreshToken = JsonPath.read(loginResponseBody, "$.data.refreshToken");

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/reissue")
                        .header("Authorization", "Bearer " + refreshToken)
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.role").value("PATIENT"))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.refreshToken").isNotEmpty());
    }

    @Test
    void 토큰_재발급_실패_만료된_토큰() throws Exception {
        // given
        Patient patient = savePatient("reissue2@test.com");
        redisTemplate.delete("refresh:" + patient.getId());
        String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(patient.getId()));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/reissue")
                        .header("Authorization", "Bearer " + refreshToken)
        );

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("U004"))
                .andExpect(jsonPath("$.message").value("토큰이 만료되었습니다."));
    }

    @Test
    void 토큰_재발급_실패_토큰_불일치() throws Exception {
        // given
        Patient patient = savePatient("reissue3@test.com");
        mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                LoginRequest.builder()
                                        .email("reissue3@test.com")
                                        .password(RAW_PASSWORD)
                                        .build()
                        ))
        );
        String tamperedToken = jwtProvider.generateAccessToken(String.valueOf(patient.getId()));

        // when
        ResultActions result = mockMvc.perform(
                post("/api/v1/auth/reissue")
                        .header("Authorization", "Bearer " + tamperedToken)
        );

        // then
        result.andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("U005"))
                .andExpect(jsonPath("$.message").value("토큰이 유효하지 않습니다. 다시 로그인해주세요."));
    }
}