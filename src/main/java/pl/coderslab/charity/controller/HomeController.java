package pl.coderslab.charity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.service.EmailServiceImpl;
import pl.coderslab.charity.service.UserOperationService;
import pl.coderslab.charity.service.UserService;

import java.util.UUID;


@Controller
public class HomeController {

    private final InstitutionRepository institutionRepository;
    private final DonationRepository donationRepository;
    private final UserService userService;
    private final EmailServiceImpl emailService;
    private final UserOperationService userOperationService;
    public HomeController(InstitutionRepository institutionRepository, DonationRepository donationRepository, UserService userService, EmailServiceImpl emailService, UserOperationService userOperationService) {
        this.institutionRepository = institutionRepository;
        this.donationRepository = donationRepository;
        this.userService = userService;
        this.emailService = emailService;
        this.userOperationService = userOperationService;
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
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userService.saveUser(user, "USER");
        String link = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link aktywacyjny do konta: " + link + " - kliknij, aby aktywować konto i móc się zalogować.";
        emailService.sendSimpleMessage(user.getEmail(), "Confirming Email", emailText);
        return "redirect:/";
    }

    @GetMapping("/activation/{uuid}")
    public String accountActivation(@PathVariable UUID uuid){
        userOperationService.activateUser(uuid);
        return "redirect:/";
    }
}
