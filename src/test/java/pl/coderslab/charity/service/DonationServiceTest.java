package pl.coderslab.charity.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithUserDetails;
import pl.coderslab.charity.exception.DonationNotFoundException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Category;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.CategoryRepository;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonationServiceTest {

    private final Long DONATION_ID = 1L;
    private final String USER_EMAIL = "sky.guy@republic.co";

    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private CategoryRepository mockCategoryRepository;
    @Mock
    private DonationRepository mockDonationRepository;
    @Mock
    private EmailService mockEmailService;
    @InjectMocks
    private DonationService donationService;

    @Test
    void shouldFindAllCategories() {
        // given
        List<Category> categories = List.of(new Category(), new Category());
        when(mockCategoryRepository.findAll()).thenReturn(categories);

        // when
        List<Category> result = donationService.findAllCategories();

        //then
        assertEquals(categories.size(), result.size());
    }

    @Test
    void findAllCategories_emptyList() {
        // given + when
        List<Category> result = donationService.findAllCategories();

        //then
        assertEquals(0, result.size());
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void shouldMakeDonation() {
        // given
        User user = new User(1L, "Anakin", USER_EMAIL, "password", 1, 0, null, null);
        Donation donation = new Donation();
        Institution institution = new Institution();
        institution.setName("Charity Institution");
        donation.setInstitution(institution);
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(userDetails.getUsername()).thenReturn(USER_EMAIL);
        when(mockUserRepository.findByEmailAndIsDeleted(USER_EMAIL, 0)).thenReturn(Optional.of(user));
        when(authentication.getPrincipal()).thenReturn(userDetails);
        doNothing().when(mockEmailService).sendSimpleMessage(anyString(), anyString(), anyString());

        // when
        String result = donationService.makeDonation(authentication, donation);

        // then
        assertEquals("Dziękujemy za przesłanie formularza. Na maila " + user.getEmail() + " prześlemy wszelkie informacje o odbiorze.", result);
        assertEquals(user, donation.getUser());
    }

    @Test
    @WithUserDetails("sky.guy@republic.co")
    void shouldMakeDonation_shouldThrowUserNotFoundException() {
        // given
        Donation donation = new Donation();
        Institution institution = new Institution();
        institution.setName("Charity Institution");
        donation.setInstitution(institution);
        UserDetails userDetails = mock(UserDetails.class);
        Authentication authentication = mock(Authentication.class);
        when(userDetails.getUsername()).thenReturn(USER_EMAIL);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // when
        UserNotFoundException thrown = assertThrows(UserNotFoundException.class,
                () -> donationService.makeDonation(authentication, donation));

        // then
        assertTrue(thrown.getMessage().contains("Nie znaleziono użytkownika."));
    }

    @Test
    void shouldFindDonationById() {
        // given
        Donation donation = new Donation();
        donation.setId(DONATION_ID);
        when(mockDonationRepository.findById(DONATION_ID)).thenReturn(Optional.of(donation));

        //when
        Donation result = donationService.findDonationById(DONATION_ID);

        //then
        assertEquals(DONATION_ID, result.getId());
    }

    @Test
    void findDonationById_shouldThrowDonationNotFoundException() {
        // given + when
        DonationNotFoundException thrown = assertThrows(DonationNotFoundException.class, () -> donationService.findDonationById(DONATION_ID));

        //then
        assertTrue(thrown.getMessage().contains("Nie znaleziono darowizny o id = " + DONATION_ID + "."));
    }

    @Test
    void shouldSetDonationArchive() {
        // given
        Donation donation = new Donation();
        donation.setId(DONATION_ID);
        LocalDate earlierDate = LocalDate.now().minusDays(1);
        when(mockDonationRepository.findById(DONATION_ID)).thenReturn(Optional.of(donation));

        // when
        donationService.setDonationArchive(DONATION_ID);

        // then
        assertEquals(1, donation.getArchived());
        assertTrue(donation.getRealPickUpDate().isAfter(earlierDate));
    }

    @Test
    void setDonationArchive_shouldThrowDonationNotFoundException() {
        // given + when
        DonationNotFoundException thrown = assertThrows(DonationNotFoundException.class, () -> donationService.setDonationArchive(DONATION_ID));

        //then
        assertTrue(thrown.getMessage().contains("Nie znaleziono darowizny o id = " + DONATION_ID + "."));
    }
}