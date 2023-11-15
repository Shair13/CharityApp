package pl.coderslab.charity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.CategoryRepository;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.EmailServiceImpl;

@Controller
@RequiredArgsConstructor
public class DonationController {

    private final DonationRepository donationRepository;
    private final CategoryRepository categoryRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;

    @GetMapping("/donation")
    public String displayDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("institutions", institutionRepository.findAllByIsDeleted(0));
        return "app/form";
    }

    @PostMapping("/donation")
    public String processDonationForm(Donation donation, BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            return "app/form";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0);
        donation.setUser(user);
        donationRepository.save(donation);
        String emailText = "Cześć " + user.getFirstName() + ", właśnie otrzymaliśmy od Ciebie zlecenie odbioru worków z darami na fundację "+ donation.getInstitution().getName() +".\nIlość worków: "+ donation.getQuantity() +"\nAdres pod jakim należy przygotować worki to: " + donation.getStreet() + ", " + donation.getZipCode() + " " + donation.getCity() + ".\nBędziemy się z Państwem kontaktować pod numerem telefonu: " + donation.getPhone() + ".\n\nSerdecznie pozdrawiamy\nZespół CharityApp";
        emailService.sendSimpleMessage(user.getEmail(), "Message from customer", emailText);
        String successMessage = "Dziękujemy za przesłanie formularza. Na maila " + user.getEmail() + " prześlemy wszelkie informacje o odbiorze.";
        model.addAttribute("message", successMessage);
        return "home/success-page";
    }


}
