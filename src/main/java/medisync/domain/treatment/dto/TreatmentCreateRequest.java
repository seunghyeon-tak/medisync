package medisync.domain.treatment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreatmentCreateRequest {
    @NotNull
    private Long appointmentId;

    @NotNull
    private Long medicalSubjectId;

    private String content;
}
