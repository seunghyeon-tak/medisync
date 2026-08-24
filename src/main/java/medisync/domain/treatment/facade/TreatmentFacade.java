package medisync.domain.treatment.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.treatment.dto.TreatmentCreateRequest;
import medisync.domain.treatment.dto.TreatmentCreateResponse;
import medisync.domain.treatment.service.TreatmentService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TreatmentFacade {
    private final TreatmentService treatmentService;

    public TreatmentCreateResponse createTreatment(Long doctorId, TreatmentCreateRequest request) {
        return treatmentService.createTreatment(
                doctorId, request.getAppointmentId(), request.getMedicalSubjectId(), request.getContent()
        );
    }
}
