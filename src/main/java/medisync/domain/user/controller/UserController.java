package medisync.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import medisync.common.response.ApiResponse;
import medisync.domain.user.dto.DoctorSignupRequest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.dto.PharmacistSignupRequest;
import medisync.domain.user.facade.UserFacade;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserFacade userFacade;

    @PostMapping("/signup/patient")
    public ApiResponse<Void> patientSignup(@Valid @RequestBody PatientSignupRequest request) {
        userFacade.patientSignup(request);

        return ApiResponse.ok();
    }

    @PostMapping("/signup/doctor")
    public ApiResponse<Void> doctorSignup(@Valid @RequestBody DoctorSignupRequest request) {
        userFacade.doctorSignup(request);

        return ApiResponse.ok();
    }

    @PostMapping("/signup/pharmacist")
    public ApiResponse<Void> pharmacistSignup(@Valid @RequestBody PharmacistSignupRequest request) {
        userFacade.pharmacistSignup(request);

        return ApiResponse.ok();
    }
}
