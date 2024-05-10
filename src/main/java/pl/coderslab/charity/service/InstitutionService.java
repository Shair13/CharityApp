package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.exception.InstitutionNotFoundException;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.repository.InstitutionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InstitutionService {

    private final InstitutionRepository institutionRepository;

    public void saveInstitution(Institution institution) {
        institutionRepository.save(institution);
    }

    public List<Institution> findAllInstitutions() {
        return institutionRepository.findAllByIsDeleted(0);
    }

    public Institution findInstitutionById(Long id) {
        return institutionRepository.findById(id).orElseThrow(InstitutionNotFoundException::new);
    }

    public void deleteInstitution(Long id) {
        Institution institution = institutionRepository.findById(id).orElseThrow(InstitutionNotFoundException::new);
        institution.setIsDeleted(1);
        institutionRepository.save(institution);
    }
}
