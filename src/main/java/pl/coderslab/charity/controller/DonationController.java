package pl.coderslab.charity.controller;

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

@Controller
public class DonationController {

    private final DonationRepository donationRepository;
    private final CategoryRepository categoryRepository;
    private final InstitutionRepository institutionRepository;
    private final UserRepository userRepository;

    public DonationController(DonationRepository donationRepository, CategoryRepository categoryRepository, InstitutionRepository institutionRepository, UserRepository userRepository) {
        this.donationRepository = donationRepository;
        this.categoryRepository = categoryRepository;
        this.institutionRepository = institutionRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/donation")
    public String displayDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("institutions", institutionRepository.findAll());
        return "app/form";
    }

    @PostMapping("/donation")
    public String processDonationForm(Donation donation, BindingResult result, Authentication authentication) {
        if (result.hasErrors()) {
            return "app/form";
        }
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        donation.setUser(user);
        donationRepository.save(donation);
        return "redirect:/";
    }


}
