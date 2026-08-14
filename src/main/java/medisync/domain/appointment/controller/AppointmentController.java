package medisync.domain.appointment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ApiResponse;
import medisync.domain.appointment.dto.AppointmentCreateRequest;
import medisync.domain.appointment.dto.AppointmentCreateResponse;
import medisync.domain.appointment.facade.AppointmentFacade;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentFacade appointmentFacade;

    @PostMapping()
    public ApiResponse<AppointmentCreateResponse> createAppointment(Authentication authentication, @Valid @RequestBody AppointmentCreateRequest request) {
        AppointmentCreateResponse response = appointmentFacade.createAppointment(Long.valueOf(authentication.getName()), request);
        return ApiResponse.ok(response);
    }
}
