package medisync.domain.user.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.pharmacy.entity.Pharmacy;
import medisync.domain.user.dto.DoctorSignupRequest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.dto.PharmacistSignupRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.Pharmacist;
import medisync.domain.user.exception.UserErrorCode;
import medisync.domain.user.exception.UserException;
import medisync.domain.user.mapper.UserMapper;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PharmacistRepository;
import medisync.domain.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final PharmacistRepository pharmacistRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    private String encodedPassword(String password) {
        return passwordEncoder.encode(password);
    }

    public void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Transactional
    public void savePatient(PatientSignupRequest request) {
        String encodedPassword = encodedPassword(request.getPassword());
        Patient patient = userMapper.patientMapper(request, encodedPassword);

        userRepository.save(patient);
    }

    @Transactional
    public void saveDoctor(DoctorSignupRequest request, Hospital hospital) {
        String encodedPassword = encodedPassword(request.getPassword());
        Doctor doctor = userMapper.doctorMapper(request, encodedPassword, hospital);

        doctorRepository.save(doctor);
    }

    @Transactional
    public void savePharmacist(PharmacistSignupRequest request, Pharmacy pharmacy) {
        String encodedPassword = encodedPassword(request.getPassword());
        Pharmacist pharmacist = userMapper.pharmacistMapper(request, encodedPassword, pharmacy);

        pharmacistRepository.save(pharmacist);
    }
}
