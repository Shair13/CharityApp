package pl.coderslab.charity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.service.DonationService;

@Controller
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @GetMapping("/donation")
    public String displayDonationForm(Model model) {
        model.addAttribute("donation", new Donation());
        model.addAttribute("categories", donationService.findAllCategories());
        model.addAttribute("institutions", donationService.findAllInstitutions());
        return "app/form";
    }

    @PostMapping("/donation")
    public String processDonationForm(Donation donation, BindingResult result, Authentication authentication, Model model) {
        if (result.hasErrors()) {
            return "app/form";
        }
        model.addAttribute("message", donationService.makeDonation(authentication, donation));
        return "home/success-page";
    }
}
