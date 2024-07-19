package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.AccountService;
import pl.coderslab.charity.service.DonationService;
import pl.coderslab.charity.service.SpringDataUserDetailsService;
import pl.coderslab.charity.service.UserOperationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final AccountService accountService;
    private final DonationService donationService;

    @GetMapping("/edit")
    public String displayEditUserForm(Model model, Authentication authentication) {
        model.addAttribute("userDTO", accountService.displayCurrentUser(authentication));
        return "profile/user-edit-form";
    }

    @PostMapping("/edit")
    public String processEditUserForm(UserDTO userDTO) {
        accountService.updateUser(userDTO);
        return "redirect:/profile";
    }

    @GetMapping("password")
    public String displayChangePasswordForm(Model model, Authentication authentication) {
        model.addAttribute("userDTO", accountService.displayCurrentUser(authentication));
        return "profile/change-password";
    }

    @PostMapping("password")
    public String processChangePassForm(@Valid PasswordDTO passwordDTO, @RequestParam String password2, BindingResult result) {
        if (result.hasErrors() && !accountService.comparePasswords(passwordDTO.getPassword(), password2)) {
            return "profile/change-password";
        }
        accountService.updatePassword(passwordDTO);
        return "redirect:/profile";
    }

    @GetMapping("/donations")
    public String showDonations(Model model, Authentication authentication) {
        model.addAttribute("donations", donationService.findAllUserDonations(authentication));
        return "profile/donations";
    }

    @GetMapping("/donation/{id}")
    public String donationDetails(Model model, @PathVariable Long id) {
        model.addAttribute("donation", donationService.findDonationById(id));
        return "profile/donation";
    }

    @GetMapping("archive/{id}")
    public String archiveDonation(@PathVariable Long id) {
        donationService.setDonationArchive(id);
        return "redirect:/profile/donations";
    }
}
