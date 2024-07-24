package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.service.*;

import java.util.UUID;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserOperationService userOperationService;
    private final HomePageService homePageService;
    private final AccountService accountService;
    private final InstitutionService institutionService;
    private final SqlInitService sqlInitService;

    @GetMapping("/")
    public String homeAction(Model model) {
        model.addAttribute("institutions", institutionService.findAllInstitutions());
        model.addAttribute("sumQuantity", homePageService.bagsQuantity());
        model.addAttribute("donations", homePageService.donationsQuantity());
        return "home/home";
    }

    @GetMapping("/registration")
    public String displayRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "home/registration-form";
    }

    @PostMapping("/registration")
    public String processRegistrationForm(@Valid User user, BindingResult result, @RequestParam String password2, Model model) {
        if (result.hasErrors() || !accountService.comparePasswords(user.getPassword(), password2)) {
            return "home/registration-form";
        }
        model.addAttribute("message", accountService.createAccount(user));
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
        User user = userOperationService.findUserByEmail(email);
        if (user == null) {
            return "error/user-not-found";
        }
        model.addAttribute("message", accountService.remindPassword(user, email));
        return "home/success-page";
    }

    @GetMapping("/newpass/{uuid}")
    public String displayNewPasswordForm(@PathVariable UUID uuid, Model model) {
        model.addAttribute("userDTO", userOperationService.getUserDTO(uuid));
        return "home/new-password-form";
    }

    @PostMapping("/newpass")
    public String processNewPasswordForm(PasswordDTO passwordDTO, @RequestParam String password2, Model model) {
        if (accountService.comparePasswords(passwordDTO.getPassword(), password2)) {
            accountService.updatePassword(passwordDTO);
            String message = "Hasło zostało zmienione! :)";
            model.addAttribute("message", message);
            return "home/success-page";
        }
        return "home/new-password-form";
    }

    @PostMapping("/contact")
    public String contactMessage(@RequestParam String name, @RequestParam String surname, @RequestParam String message, Model model) {
        homePageService.sendMessage(name, surname, message, model);
        return "home/success-page";
    }

    @GetMapping("/sqlinit")
    public String initrialSqlMethod(Model model) {
        return sqlInitService.sqlInit(model);
    }
}