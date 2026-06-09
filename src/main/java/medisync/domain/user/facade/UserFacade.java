package medisync.domain.user.facade;

import lombok.RequiredArgsConstructor;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    @Transactional
    public void patientSignup(PatientSignupRequest request) {
        userService.validateDuplicateEmail(request.getEmail());

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new UserException(UserErrorCode.PASSWORD_MISMATCH);
        }

        userService.savePatient(request);
    }
}
