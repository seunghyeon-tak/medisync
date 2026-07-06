package medisync.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ApiResponse;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.dto.LoginResponse;
import medisync.domain.user.facade.AuthFacade;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthFacade authFacade;

    private String extractToken(String token) {
        // Bearer 제거
        return token.substring(7);
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authFacade.login(request);

        return ApiResponse.ok(response);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String accessToken) {
        authFacade.logout(extractToken(accessToken));

        return ApiResponse.ok();
    }

    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissueToken(@RequestHeader("Authorization") String refreshToken) {
        LoginResponse response = authFacade.reissueToken(extractToken(refreshToken));

        return ApiResponse.ok(response);
    }
}
