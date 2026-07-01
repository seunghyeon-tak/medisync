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

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authFacade.login(request);

        return ApiResponse.ok(response);
    }

    @PostMapping("/reissue")
    public ApiResponse<LoginResponse> reissueToken(@RequestHeader("Authorization") String refreshToken) {
        // Bearer 제거
        String token = refreshToken.substring(7);
        LoginResponse response = authFacade.reissueToken(token);

        return ApiResponse.ok(response);
    }
}
