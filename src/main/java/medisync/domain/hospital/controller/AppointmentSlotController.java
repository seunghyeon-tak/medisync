package medisync.domain.hospital.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ApiResponse;
import medisync.domain.hospital.dto.AppointmentSlotCreateRequest;
import medisync.domain.hospital.dto.AppointmentSlotCreateResponse;
import medisync.domain.hospital.facade.AppointmentSlotFacade;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointment-slots")
@RequiredArgsConstructor
public class AppointmentSlotController {
    private final AppointmentSlotFacade appointmentSlotFacade;

    @PostMapping()
    public ApiResponse<AppointmentSlotCreateResponse> createSlot(Authentication authentication, @Valid @RequestBody AppointmentSlotCreateRequest request) {
        AppointmentSlotCreateResponse response = appointmentSlotFacade.createSlot(Long.valueOf(authentication.getName()), request);

        return ApiResponse.ok(response);
    }
}
