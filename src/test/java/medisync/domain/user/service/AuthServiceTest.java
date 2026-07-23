package medisync.domain.user.service;

import common.BaseServiceTest;
import medisync.common.jwt.JwtProvider;
import medisync.domain.user.dto.LoginResponse;
import medisync.domain.user.entity.User;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthServiceTest extends BaseServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private AuthService authService;

    private static final String TEST_PASSWORD = "testPassword1!";
    private static final String TEST_WRONG_PASSWORD = "ttttPassword1!";

    @Test
    void 이메일_존재하면_유저반환() {
        // given
        String email = "tester1@test.com";
        User user = User.builder()
                .email(email)
                .build();

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        User result = authService.validateEmail(email);

        // then
        assertEquals(user, result);
    }

    @Test
    void 이메일_없으면_예외발생() {
        // given
        String email = "notfound@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when & then
        assertThrows(UserException.class, () -> authService.validateEmail(email));
    }

    @Test
    void 비밀번호_일치() {
        // given
        User user = User.builder()
                .password(TEST_PASSWORD)
                .build();
        when(passwordEncoder.matches(TEST_PASSWORD, user.getPassword())).thenReturn(true);

        // when & then
        assertDoesNotThrow(() -> authService.validatePassword(user, TEST_PASSWORD));
    }

    @Test
    void 비밀번호_불일치() {
        // given
        User user = User.builder()
                .password(TEST_PASSWORD)
                .build();
        when(passwordEncoder.matches(TEST_WRONG_PASSWORD, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(UserException.class, () -> authService.validatePassword(user, TEST_WRONG_PASSWORD));
    }

    @Test
    void 토큰생성() {
        // given
        User user = User.builder()
                .name("테스터1")
                .email("tester1@test.com")
                .password(TEST_PASSWORD)
                .role(Role.PATIENT)
                .build();
        ReflectionTestUtils.setField(authService, "refreshTokenExpiration", 1000L);

        when(jwtProvider.generateAccessToken(String.valueOf(user.getId()))).thenReturn("access-token");
        when(jwtProvider.generateRefreshToken(String.valueOf(user.getId()))).thenReturn("refresh-token");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        LoginResponse response = authService.generateTokens(user);

        // then
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals(user.getRole(), response.getRole());
        verify(redisTemplate).delete("refresh:" + user.getId());
        verify(valueOperations).set(
                eq("refresh:" + user.getId()),
                eq("refresh-token"),
                eq(1000L),
                eq(TimeUnit.MILLISECONDS)
        );
    }

    @Test
    void 리프레쉬토큰_정상_검증() {
        // given
        String refreshToken = "refresh-token";
        User user = User.builder()
                .name("테스터1")
                .email("test2@test.com")
                .password(TEST_PASSWORD)
                .role(Role.PATIENT)
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);

        when(jwtProvider.getSubject(refreshToken)).thenReturn(String.valueOf(user.getId()));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:" + user.getId())).thenReturn(refreshToken);

        // when
        Long userId = authService.validateRefreshToken(refreshToken);

        // then
        assertEquals(user.getId(), userId);
    }

    @Test
    void 리프레쉬토큰_만료() {
        // given
        String refreshToken = "refresh-token";

        when(jwtProvider.getSubject(refreshToken)).thenReturn("1");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:1")).thenReturn(null);

        // when
        UserException exception = assertThrows(
                UserException.class,
                () -> authService.validateRefreshToken(refreshToken));

        // then
        assertEquals(UserErrorCode.TOKEN_HAS_EXPIRED, exception.getUserErrorCode());
    }

    @Test
    void 리프레쉬토큰_불일치() {
        // given
        String refreshToken = "refresh-token";

        when(jwtProvider.getSubject(refreshToken)).thenReturn("1");
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get("refresh:1")).thenReturn("different-token");

        // when
        UserException exception = assertThrows(
                UserException.class,
                () -> authService.validateRefreshToken(refreshToken));

        // then
        assertEquals(UserErrorCode.INVALID_REFRESH_TOKEN, exception.getUserErrorCode());
        verify(redisTemplate).delete("refresh:1");
    }

    @Test
    void 블랙리스트_등록() {
        // given
        String token = "token";
        when(jwtProvider.getSubject(token)).thenReturn("1");
        when(jwtProvider.getRemainingExpiration(token)).thenReturn(1000L);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        // when
        authService.addBlacklist(token);

        // then
        verify(valueOperations).set(
                eq("blacklist:" + token),
                eq("logout"),
                eq(1000L),
                eq(TimeUnit.MILLISECONDS)
        );
        verify(redisTemplate).delete("refresh:1");
    }
}