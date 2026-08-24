package medisync.domain.treatment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import common.BaseControllerTest;
import medisync.domain.appointment.entity.Appointment;
import medisync.domain.appointment.entity.enums.CallType;
import medisync.domain.appointment.repository.AppointmentRepository;
import medisync.domain.hospital.entity.AppointmentSlot;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.repository.AppointmentSlotRepository;
import medisync.domain.hospital.repository.HospitalRepository;
import medisync.domain.medicalsubject.entity.MedicalSubject;
import medisync.domain.medicalsubject.repository.MedicalSubjectRepository;
import medisync.domain.treatment.dto.TreatmentCreateRequest;
import medisync.domain.treatment.entity.Treatment;
import medisync.domain.treatment.repository.TreatmentRepository;
import medisync.domain.user.dto.LoginRequest;
import medisync.domain.user.entity.Doctor;
import medisync.domain.user.entity.Patient;
import medisync.domain.user.entity.enums.BloodType;
import medisync.domain.user.entity.enums.Role;
import medisync.domain.user.repository.DoctorRepository;
import medisync.domain.user.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TreatmentControllerTest extends BaseControllerTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private AppointmentSlotRepository appointmentSlotRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private MedicalSubjectRepository medicalSubjectRepository;

    @Autowired
    private TreatmentRepository treatmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final String RAW_PASSWORD = "testPassword1!";

    private Doctor saveDoctor(String email) {
        Hospital hospital = Hospital.builder()
                .name("테스트병원1")
                .address("서울시 강남구")
                .phone("02-3333-1111")
                .build();
        hospitalRepository.save(hospital);

        Doctor doctor = Doctor.builder()
                .name("의사테스터")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1989-01-01"))
                .role(Role.DOCTOR)
                .phone("010-2222-3333")
                .hospital(hospital)
                .licenseNumber("111111111")
                .build();
        return doctorRepository.save(doctor);
    }

    private Patient savePatient(String email) {
        Patient patient = Patient.builder()
                .name("테스터환자1")
                .email(email)
                .password(passwordEncoder.encode(RAW_PASSWORD))
                .birthDay(LocalDate.parse("1990-01-01"))
                .address("서울시 강남구")
                .role(Role.PATIENT)
                .phone("010-1234-1111")
                .bloodType(BloodType.RHPlusA)
                .build();
        return patientRepository.save(patient);
    }

    private MedicalSubject saveMedicalSubject() {
        MedicalSubject medicalSubject = MedicalSubject.builder()
                .name("내과")
                .build();
        return medicalSubjectRepository.save(medicalSubject);
    }

    private Appointment saveAppointment(Doctor doctor, Patient patient) {
        AppointmentSlot appointmentSlot = AppointmentSlot.builder()
                .doctor(doctor)
                .date(LocalDate.now().plusDays(1))
                .startTime(LocalTime.parse("10:00:00"))
                .endTime(LocalTime.parse("11:00:00"))
                .build();
        appointmentSlotRepository.save(appointmentSlot);

        Appointment appointment = Appointment.create(patient, appointmentSlot, "", "", CallType.VOICE);
        return appointmentRepository.save(appointment);
    }

    private String getAccessToken(String email) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email(email)
                .password(RAW_PASSWORD)
                .build();

        String responseBody = mockMvc.perform(
                post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andReturn().getResponse().getContentAsString();

        return JsonPath.read(responseBody, "$.data.accessToken");
    }

    @Test
    void 진료_완료_성공() throws Exception {
        // given
        String email = "testerDoctor1@test.com";
        Doctor doctor = saveDoctor(email);
        Patient patient = savePatient("testerPa1@test.com");
        Appointment appointment = saveAppointment(doctor, patient);
        MedicalSubject medicalSubject = saveMedicalSubject();
        String accessToken = getAccessToken(email);

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(appointment.getId())
                .medicalSubjectId(medicalSubject.getId())
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").isNotEmpty());
    }

    @Test
    void 진료_실패_예약_없음() throws Exception {
        // given
        String email = "testerDoctor1@test.com";
        saveDoctor(email);
        MedicalSubject medicalSubject = saveMedicalSubject();
        String accessToken = getAccessToken(email);

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(99999L)
                .medicalSubjectId(medicalSubject.getId())
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("A001"))
                .andExpect(jsonPath("$.message").value("예약된 내용이 없습니다."));
    }

    @Test
    void 진료_실패_담당_의사_아님() throws Exception {
        // given
        String ownerEmail = "testerDoctor1@test.com";
        String otherEmail = "testerDoctor2@test.com";
        Doctor ownerDoctor = saveDoctor(ownerEmail);
        saveDoctor(otherEmail);
        Patient patient = savePatient("testerPa1@test.com");
        Appointment appointment = saveAppointment(ownerDoctor, patient);
        MedicalSubject medicalSubject = saveMedicalSubject();
        String accessToken = getAccessToken(otherEmail);

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(appointment.getId())
                .medicalSubjectId(medicalSubject.getId())
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("A002"))
                .andExpect(jsonPath("$.message").value("예약한 진료의 담당 의사가 아닙니다."));
    }

    @Test
    void 진료_실패_진료과목_없음() throws Exception {
        // given
        String email = "testerDoctor1@test.com";
        Doctor doctor = saveDoctor(email);
        Patient patient = savePatient("testerPa1@test.com");
        Appointment appointment = saveAppointment(doctor, patient);
        String accessToken = getAccessToken(email);

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(appointment.getId())
                .medicalSubjectId(99999L)
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("M001"))
                .andExpect(jsonPath("$.message").value("찾는 진료과목이 없습니다."));
    }

    @Test
    void 진료_실패_이미_진료_완료() throws Exception {
        // given
        String email = "testerDoctor1@test.com";
        Doctor doctor = saveDoctor(email);
        Patient patient = savePatient("testerPa1@test.com");
        Appointment appointment = saveAppointment(doctor, patient);
        MedicalSubject medicalSubject = saveMedicalSubject();
        String accessToken = getAccessToken(email);

        Treatment treatment = Treatment.create(appointment, medicalSubject, "이미 완료된 진료");
        treatmentRepository.save(treatment);
        appointment.reservationStatusComplete();

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(appointment.getId())
                .medicalSubjectId(medicalSubject.getId())
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("T002"))
                .andExpect(jsonPath("$.message").value("이미 진료가 존재합니다."));
    }

    @Test
    void 진료_실패_인증_없음() throws Exception {
        // given
        String accessToken = "";

        TreatmentCreateRequest request = TreatmentCreateRequest.builder()
                .appointmentId(9999L)
                .medicalSubjectId(9999L)
                .content("감기 증상으로 진료 완료")
                .build();

        // when
        ResultActions result = mockMvc.perform(post("/api/v1/treatments")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        );

        // then
        result.andExpect(status().isForbidden());
    }
}