package medisync.domain.treatment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ApiResponse;
import medisync.domain.treatment.dto.TreatmentCreateRequest;
import medisync.domain.treatment.dto.TreatmentCreateResponse;
import medisync.domain.treatment.facade.TreatmentFacade;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/treatments")
@RequiredArgsConstructor
public class TreatmentController {
    private final TreatmentFacade treatmentFacade;

    @PostMapping()
    public ApiResponse<TreatmentCreateResponse> createTreatment(Authentication authentication, @Valid @RequestBody TreatmentCreateRequest request) {
        TreatmentCreateResponse response = treatmentFacade.createTreatment(Long.valueOf(authentication.getName()), request);

        return ApiResponse.ok(response);
    }
}
