package medisync.domain.hospital.service;

import common.BaseServiceTest;
import medisync.domain.hospital.entity.Hospital;
import medisync.domain.hospital.exception.HospitalErrorCode;
import medisync.domain.hospital.exception.HospitalException;
import medisync.domain.hospital.repository.HospitalRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class HospitalServiceTest extends BaseServiceTest {
    @Mock
    private HospitalRepository hospitalRepository;

    @InjectMocks
    private HospitalService hospitalService;

    @Test
    void 병원_존재() {
        // given
        Long hospitalId = 1L;
        Hospital hospital = Hospital.builder().build();
        ReflectionTestUtils.setField(hospital, "id", hospitalId);
        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.of(hospital));

        // when
        Hospital result = hospitalService.getHospital(hospitalId);

        // then
        assertEquals(hospital, result);
        assertEquals(hospitalId, result.getId());
    }

    @Test
    void 병원_없음() {
        // given
        Long hospitalId = 3L;
        when(hospitalRepository.findById(hospitalId)).thenReturn(Optional.empty());

        // when
        HospitalException exception = assertThrows(
                HospitalException.class, () -> hospitalService.getHospital(hospitalId));

        // then
        assertEquals(HospitalErrorCode.HOSPITAL_NOT_FOUND, exception.getHospitalErrorCode());
    }
}