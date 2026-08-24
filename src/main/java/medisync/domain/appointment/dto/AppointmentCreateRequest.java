package medisync.domain.appointment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import medisync.domain.appointment.entity.enums.CallType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentCreateRequest {
    @NotNull
    private Long slotId;

    private String symptom;

    private String picture;

    @NotNull
    private CallType callType;
}
