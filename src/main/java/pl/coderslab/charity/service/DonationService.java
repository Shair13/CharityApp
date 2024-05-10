package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Category;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.CategoryRepository;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final InstitutionRepository institutionRepository;
    private final DonationRepository donationRepository;
    private final EmailServiceImpl emailService;


    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Institution> findAllInstitutions() {
        return institutionRepository.findAllByIsDeleted(0);
    }

    public void makeDonation(Authentication authentication, Donation donation, Model model) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(UserNotFoundException::new);
        donation.setUser(user);
        donationRepository.save(donation);

        String emailText = "Cześć " + user.getFirstName() + ", właśnie otrzymaliśmy od Ciebie zlecenie odbioru worków z darami na fundację "
                + donation.getInstitution().getName() + ".\nIlość worków: " + donation.getQuantity() + "\nAdres pod jakim należy przygotować worki to: "
                + donation.getStreet() + ", " + donation.getZipCode() + " " + donation.getCity() + ".\nBędziemy się z Państwem kontaktować pod numerem telefonu: "
                + donation.getPhone() + ".\n\nSerdecznie pozdrawiamy\nZespół CharityApp";

        emailService.sendSimpleMessage(user.getEmail(), "Message from customer", emailText);
        String successMessage = "Dziękujemy za przesłanie formularza. Na maila " + user.getEmail() + " prześlemy wszelkie informacje o odbiorze.";
        model.addAttribute("message", successMessage);
    }
}
