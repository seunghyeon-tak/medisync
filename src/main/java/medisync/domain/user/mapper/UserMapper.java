package medisync.domain.user.mapper;

import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public Patient patientMapper(PatientSignupRequest request, String encodedPassword) {
        return Patient.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .birthDay(request.getBirthDay())
                .address(request.getAddress())
                .role(Role.PATIENT)
                .phone(request.getPhone())
                .bloodType(request.getBloodType())
                .medicalHistory(request.getMedicalHistory())
                .currentMedications(request.getCurrentMedications())
                .build();
    }
}
