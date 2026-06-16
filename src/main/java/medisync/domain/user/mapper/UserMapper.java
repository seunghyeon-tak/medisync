package medisync.domain.user.mapper;

import medisync.domain.hospital.entity.Hospital;
import medisync.domain.user.dto.DoctorSignupRequest;
import medisync.domain.user.dto.PatientSignupRequest;
import medisync.domain.user.dto.PharmacistSignupRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.Pharmacist;
import medisync.domain.user.entity.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    public Doctor doctorMapper(DoctorSignupRequest request, String encodedPassword, Hospital hospital) {
        return Doctor.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .birthDay(request.getBirthDay())
                .address(request.getAddress())
                .role(Role.DOCTOR)
                .phone(request.getPhone())
                .hospital(hospital)
                .licenseNumber(request.getLicenseNumber())
                .build();
    }

    public Pharmacist pharmacistMapper(PharmacistSignupRequest request, String encodedPassword) {
        return Pharmacist.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(encodedPassword)
                .birthDay(request.getBirthDay())
                .address(request.getAddress())
                .role(Role.PHARMACIST)
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .build();
    }
}
