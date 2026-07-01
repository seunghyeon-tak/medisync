package medisync.domain.user.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.dto.LoginResponse;
import medisync.domain.user.entity.User;
import medisync.domain.user.service.AuthService;
import medisync.domain.user.service.UserService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthFacade {
    private final AuthService authService;
    private final UserService userService;

    public LoginResponse login(LoginRequest request) {
        // 가입된 이메일인지 확인
        User user = authService.validateEmail(request.getEmail());

        // 비밀번호가 맞는지 확인
        authService.validatePassword(user, request.getPassword());

        // accessToken & refreshToken 발급된거 반환
        return authService.generateTokens(user);
    }

    public LoginResponse reissueToken(String refreshToken) {
        // 일치 여부 확인
        Long userId = authService.validateRefreshToken(refreshToken);
        User user = userService.getUser(userId);

        // 새 토큰 발급 + 기존 토큰 폐기
        return authService.generateTokens(user);
    }
}
