package medisync.domain.user.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.mapper.UserMapper;
import medisync.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Transactional
    public void savePatient(PatientSignupRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        Patient patient = userMapper.patientMapper(request, encodedPassword);

        userRepository.save(patient);
    }
}
