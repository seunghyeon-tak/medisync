package medisync.domain.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import medisync.domain.user.entity.enums.BloodType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PatientSignupRequest extends UserSignupRequest {
    @NotNull
    private BloodType bloodType;

    private String medicalHistory;

    private String currentMedications;
}
