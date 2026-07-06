package medisync.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.domain.user.entity.enums.Role;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class UserSignupRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Size(min = 8)
    @NotBlank
    private String password;

    @Size(min = 8)
    @NotBlank
    private String passwordConfirm;

    @NotNull
    private LocalDate birthDay;

    private String address;

    @NotNull
    private Role role;

    @NotBlank
    private String phone;
}
