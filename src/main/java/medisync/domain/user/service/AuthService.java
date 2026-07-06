package medisync.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import medisync.common.jwt.JwtProvider;
import medisync.domain.user.dto.LoginResponse;
import medisync.domain.user.entity.User;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    public User validateEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException(UserErrorCode.UNABLE_LOGIN));
    }

    public void validatePassword(User user, String rawPassword) {
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new UserException(UserErrorCode.UNABLE_LOGIN);
        }
    }

    public LoginResponse generateTokens(User user) {
        String accessToken = jwtProvider.generateAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtProvider.generateRefreshToken(String.valueOf(user.getId()));

        redisTemplate.delete("refresh:" + user.getId());

        redisTemplate.opsForValue().set(
                "refresh:" + user.getId(),
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        return LoginResponse.builder()
                .role(user.getRole())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public Long validateRefreshToken(String refreshToken) {
        // refreshToken에서 userId 추출
        String userId = jwtProvider.getSubject(refreshToken);

        Object token = redisTemplate.opsForValue().get("refresh:" + userId);

        if (token == null) {
            // 만료된 토큰
            throw new UserException(UserErrorCode.TOKEN_HAS_EXPIRED);
        } else if (!token.equals(refreshToken)) {
            // 유효하지 않은 토큰
            redisTemplate.delete("refresh:" + userId);
            throw new UserException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        return Long.valueOf(userId);
    }

    public void addBlacklist(String token) {
        String userId = jwtProvider.getSubject(token);
        long remainingExpiration = jwtProvider.getRemainingExpiration(token);

        // 블랙리스트 추가
        redisTemplate.opsForValue().set(
                "blacklist:" + token,
                "logout",
                remainingExpiration,
                TimeUnit.MILLISECONDS
        );

        // refresh token 삭제
        redisTemplate.delete("refresh:" + userId);
    }
}
