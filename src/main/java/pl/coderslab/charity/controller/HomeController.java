package pl.coderslab.charity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.UserService;


@Controller
public class HomeController {

    private final InstitutionRepository institutionRepository;
    private final DonationRepository donationRepository;
    private final UserService userService;
    public HomeController(InstitutionRepository institutionRepository, DonationRepository donationRepository, UserService userService) {
        this.institutionRepository = institutionRepository;
        this.donationRepository = donationRepository;
        this.userService = userService;
    }

    @GetMapping("/")
    public String homeAction(Model model){
        model.addAttribute("institutions", institutionRepository.findAll());
        model.addAttribute("sumQuantity", donationRepository.sumQuantity());
        model.addAttribute("donations", donationRepository.findAll().size());
        return "home/home";
    }

    @GetMapping("/registration")
    public String displayRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "home/registration-form";
    }

    @PostMapping("/registration")
    public String processRegistrationForm(User user, BindingResult result, @RequestParam String password2){
        if(result.hasErrors() || !user.getPassword().equals(password2)){
            return "home/registration-form";
        }
        userService.saveUser(user, "USER");
        return "redirect:/";
    }
}
