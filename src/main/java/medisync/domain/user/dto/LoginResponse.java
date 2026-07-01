package medisync.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medisync.domain.user.entity.enums.Role;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {
    private Role role;
    private String accessToken;
    private String refreshToken;
}
