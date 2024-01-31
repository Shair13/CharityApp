package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.Banner;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.EmailServiceImpl;
import pl.coderslab.charity.service.UserOperationService;
import pl.coderslab.charity.service.UserService;

import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final InstitutionRepository institutionRepository;
    private final DonationRepository donationRepository;
    private final UserService userService;
    private final EmailServiceImpl emailService;
    private final UserOperationService userOperationService;
    private final UserRepository userRepository;

    @GetMapping("/")
    public String homeAction(Model model) {
        model.addAttribute("institutions", institutionRepository.findAll());
        model.addAttribute("sumQuantity", donationRepository.sumQuantity());
        model.addAttribute("donations", donationRepository.findAll().size());
        return "home/home";
    }

    @GetMapping("/registration")
    public String displayRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "home/registration-form";
    }

    @PostMapping("/registration")
    public String processRegistrationForm(@Valid User user, BindingResult result, @RequestParam String password2, Model model) {
        if (result.hasErrors() || !user.getPassword().equals(password2)) {
            return "home/registration-form";
        }
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userService.saveUser(user, "USER");
        String link = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link aktywacyjny do Twojego konta: " + link + " - kliknij, aby aktywować konto i móc się zalogować.";
        emailService.sendSimpleMessage(user.getEmail(), "Account confirmation", emailText);
        String message = "Udało sie! Zajrzyj na maila, tam znajdziesz link aktywacyjny do Twojego konta.";
        model.addAttribute("message", message);
        return "home/success-page";
    }

    @GetMapping("/activation/{uuid}")
    public String accountActivation(@PathVariable UUID uuid, Model model) {
        userOperationService.activateUser(uuid);
        String message = "Konto zostało aktywowane! Teraz możesz się zalogować :)";
        model.addAttribute("message", message);
        return "home/success-page";
    }

    @GetMapping("/reminder")
    public String displayPasswordReminder() {
        return "home/password-reminder";
    }

    @PostMapping("/reminder")
    public String passwordReminder(@RequestParam String email, Model model) {
        User user = userService.findByEmail(email);
        if (user != null) {
            UUID uuid = UUID.randomUUID();
            user.setUuid(uuid);
            userRepository.save(user);
            String link = "http://localhost:8080/newpass/" + user.getUuid();
            String emailText = "Cześć " + user.getFirstName() + ", oto link do zresetowania hasła: " + link + " - kliknij, aby przejść do formularza i ustawić nowe hasło.";
            emailService.sendSimpleMessage(email, "Reset password", emailText);
            String message = "Udało sie! Zajrzyj na maila, tam znajdziesz link do formularza zmiany hasła.";
            model.addAttribute("message", message);
            return "home/success-page";
        }
        return "error/user-not-found";
    }

    @GetMapping("/newpass/{uuid}")
    public String displayNewPasswordForm(@PathVariable UUID uuid, Model model) {
        User user = userRepository.findAllByUuid(uuid);
        model.addAttribute("userDTO", userOperationService.getUserDTO(user.getId()));
        return "home/new-password-form";
    }

    @PostMapping("/newpass")
    public String processNewPasswordForm(UserDTO userDTO, @RequestParam String password2, Model model) {
        userOperationService.updatePassword(userDTO, password2);
        String message = "Hasło zostało zmienione! :)";
        model.addAttribute("message", message);
        return "home/success-page";
    }

    @PostMapping("/contact")
    public String contactMessage(@RequestParam String name, @RequestParam String surname, @RequestParam String message, Model model) {
        String emailText = "Message from " + name + " " + surname + ": " + message;
        emailService.sendSimpleMessage("charityapp2024@gmail.com", "Message from customer", emailText);
        String succesMessage = "Wiadomość została wysłana. Odpowiemy najszybciej jak to tylko możliwe.";
        model.addAttribute("message", succesMessage);
        return "home/success-page";
    }
}
