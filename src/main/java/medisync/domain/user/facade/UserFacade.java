package medisync.domain.user.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.service.HospitalService;
import medisync.domain.user.dto.DoctorSignupRequest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.dto.PharmacistSignupRequest;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;
    private final HospitalService hospitalService;

    private void validatePassword(String password, String passwordConfirm) {
        if (!password.equals(passwordConfirm)) {
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }
    }

    @Transactional
    public void patientSignup(PatientSignupRequest request) {
        userService.validateDuplicateEmail(request.getEmail());
        validatePassword(request.getPassword(), request.getPasswordConfirm());
        userService.savePatient(request);
    }

    @Transactional
    public void doctorSignup(DoctorSignupRequest request) {
        userService.validateDuplicateEmail(request.getEmail());
        Hospital hospital = hospitalService.getHospital(request.getHospitalId());
        validatePassword(request.getPassword(), request.getPasswordConfirm());
        userService.saveDoctor(request, hospital);
    }

    @Transactional
    public void pharmacistSignup(PharmacistSignupRequest request) {
        userService.validateDuplicateEmail(request.getEmail());
        validatePassword(request.getPassword(), request.getPasswordConfirm());
        userService.savePharmacist(request);
    }
}
