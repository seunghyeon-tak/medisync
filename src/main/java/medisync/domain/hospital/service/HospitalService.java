package medisync.domain.hospital.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.exception.HospitalErrorCode;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.repository.HospitalRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HospitalService {
    private final HospitalRepository hospitalRepository;

    public Hospital getHospital(Long hospitalId) {
        return hospitalRepository
                .findById(hospitalId)
                .orElseThrow(() -> new HospitalException(HospitalErrorCode.HOSPITAL_NOT_FOUND));
    }
}
