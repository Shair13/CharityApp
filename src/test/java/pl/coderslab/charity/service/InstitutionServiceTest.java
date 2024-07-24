package pl.coderslab.charity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.coderslab.charity.exception.InstitutionNotFoundException;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.repository.InstitutionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InstitutionServiceTest {

    private final Long ID = 1L;
    private final String NAME = "New Hope";
    private final String DESCRIPTION = "Help me OBI-WAN KENOBI!";

    @Mock
    private InstitutionRepository mockInstitutionRepository;
    @InjectMocks
    private InstitutionService institutionService;

    @Test
    void shouldFindAllInstitutions() {
        // given
        List<Institution> institutions = List.of(new Institution(), new Institution());
        when(mockInstitutionRepository.findAllByIsDeleted(0)).thenReturn(institutions);

        // when
        List<Institution> result = institutionService.findAllInstitutions();

        //then
        assertEquals(institutions.size(), result.size());
    }

    @Test
    void findInstitutionById() {
        // given
        Institution institution = new Institution(ID, NAME, DESCRIPTION, 0);
        when(mockInstitutionRepository.findById(ID)).thenReturn(Optional.of(institution));

        // when
        Institution result = institutionService.findInstitutionById(ID);

        // then
        assertEquals(ID, result.getId());
        assertEquals(NAME, result.getName());
        assertEquals(DESCRIPTION, result.getDescription());
        assertEquals(0, result.getIsDeleted());
    }

    @Test
    void findInstitutionById_shouldThrowInstitutionNotFoundException() {
        // given + when
        InstitutionNotFoundException thrown = assertThrows(InstitutionNotFoundException.class,
                () -> institutionService.findInstitutionById(ID));
        // then
        assertTrue(thrown.getMessage().contains("Nie znaleziono instytucji."));
    }

    @Test
    void deleteRecoverInstitutionToggle_shouldRemoveInstitution() {
        // given
        Institution institution = new Institution(ID, NAME, DESCRIPTION, 0);
        when(mockInstitutionRepository.findById(ID)).thenReturn(Optional.of(institution));

        // when
        institutionService.deleteRecoverInstitutionToggle(ID);

        // then
        assertEquals(1, institution.getIsDeleted());
    }

    @Test
    void deleteRecoverInstitutionToggle_shouldRecoverInstitution() {
        // given
        Institution institution = new Institution(ID, NAME, DESCRIPTION, 1);
        when(mockInstitutionRepository.findById(ID)).thenReturn(Optional.of(institution));

        // when
        institutionService.deleteRecoverInstitutionToggle(ID);

        // then
        assertEquals(0, institution.getIsDeleted());
    }
    @Test
    void deleteRecoverInstitutionToggle_shouldThrowInstitutionNotFoundException() {
        // given + when
        InstitutionNotFoundException thrown = assertThrows(InstitutionNotFoundException.class,
                () -> institutionService.deleteRecoverInstitutionToggle(ID));
        // then
        assertTrue(thrown.getMessage().contains("Nie znaleziono instytucji."));
    }

}