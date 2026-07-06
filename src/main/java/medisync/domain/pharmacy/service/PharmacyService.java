package medisync.domain.pharmacy.service;

import lombok.RequiredArgsConstructor;
import medisync.domain.pharmacy.entity.Pharmacy;
import medisync.domain.pharmacy.exception.PharmacyErrorCode;
import medisync.domain.pharmacy.exception.PharmacyException;
import medisync.domain.pharmacy.repository.PharmacyRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;

    public Pharmacy getPharmacy(Long pharmacyId) {
        return pharmacyRepository
                .findById(pharmacyId)
                .orElseThrow(() -> new PharmacyException(PharmacyErrorCode.PHARMACY_NOT_FOUND));
    }
}
