package medisync.domain.pharmacy.service;

import common.BaseServiceTest;
import medisync.domain.pharmacy.entity.Pharmacy;
import medisync.domain.pharmacy.exception.PharmacyErrorCode;
import medisync.domain.pharmacy.exception.PharmacyException;
import medisync.domain.pharmacy.repository.PharmacyRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class PharmacyServiceTest extends BaseServiceTest {
    @Mock
    private PharmacyRepository pharmacyRepository;

    @InjectMocks
    private PharmacyService pharmacyService;

    @Test
    void 약국_존재() {
        // given
        Long pharmacyId = 1L;
        Pharmacy pharmacy = Pharmacy.builder().build();
        ReflectionTestUtils.setField(pharmacy, "id", pharmacyId);
        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.of(pharmacy));

        // when
        Pharmacy result = pharmacyService.getPharmacy(pharmacyId);

        // then
        assertEquals(pharmacy, result);
        assertEquals(pharmacyId, result.getId());
    }

    @Test
    void 약국_없음() {
        // given
        Long pharmacyId = 3L;
        when(pharmacyRepository.findById(pharmacyId)).thenReturn(Optional.empty());

        // when
        PharmacyException exception = assertThrows(
                PharmacyException.class, () -> pharmacyService.getPharmacy(pharmacyId));

        // then
        assertEquals(PharmacyErrorCode.PHARMACY_NOT_FOUND, exception.getPharmacyErrorCode());
    }
}