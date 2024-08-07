package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.exception.DonationNotFoundException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Category;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.CategoryRepository;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final DonationRepository donationRepository;
    private final EmailService emailService;


    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Donation> findAllUserDonations(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(() -> new UserNotFoundException(email));
        return donationRepository.findAllSortedByArchivedAndPickUpDate(user);
    }

    public Donation findDonationById(Long id) {
        return donationRepository.findById(id).orElseThrow(() -> new DonationNotFoundException(id));
    }

    public String makeDonation(Authentication authentication, Donation donation) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(() -> new UserNotFoundException(email));
        donation.setUser(user);
        donationRepository.save(donation);

        String emailText = "Cześć " + user.getFirstName() + ", właśnie otrzymaliśmy od Ciebie zlecenie odbioru worków z darami na fundację "
                + donation.getInstitution().getName() + ".\nIlość worków: " + donation.getQuantity() + "\nAdres pod jakim należy przygotować worki to: "
                + donation.getStreet() + ", " + donation.getZipCode() + " " + donation.getCity() + ".\nBędziemy się z Państwem kontaktować pod numerem telefonu: "
                + donation.getPhone() + ".\n\nSerdecznie pozdrawiamy\nZespół CharityApp";

        emailService.sendSimpleMessage(user.getEmail(), "Message from customer", emailText);

        return "Dziękujemy za przesłanie formularza. Na maila " + user.getEmail() + " prześlemy wszelkie informacje o odbiorze.";
    }

    public void setDonationArchive(Long id) {
        Donation donation = donationRepository.findById(id).orElseThrow(() -> new DonationNotFoundException(id));
        donation.setArchived(1);
        donation.setRealPickUpDate(LocalDate.now());
        donation.setRealPickUpTime(LocalTime.now());
        donationRepository.save(donation);
    }
}
