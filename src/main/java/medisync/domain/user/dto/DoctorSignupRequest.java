package medisync.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DoctorSignupRequest extends UserSignupRequest {

    @NotNull
    private Long hospitalId;

    @NotBlank
    private String licenseNumber;
}
