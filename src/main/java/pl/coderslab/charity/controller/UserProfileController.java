package pl.coderslab.charity.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.Donation;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.SpringDataUserDetailsService;
import pl.coderslab.charity.service.UserOperationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserRepository userRepository;
    private final SpringDataUserDetailsService springDataUserDetailsService;
    private final UserOperationService userOperationService;

    private final DonationRepository donationRepository;


    @GetMapping("/edit")
    public String displayEditUserForm(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        model.addAttribute("userDTO", userDTO);
        return "profile/user-edit-form";
    }

    @PostMapping("/edit")
    public String processEditUserForm(UserDTO userDTO){
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        optionalUser.ifPresent(u -> {
            u.setEmail(userDTO.getEmail());
            u.setFirstName(userDTO.getFirstName());
            userRepository.save(u);
            UserDetails updatedUserDetails = springDataUserDetailsService.loadUserByUsername(userDTO.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
        return "redirect:/profile";
    }

    @GetMapping("password")
    public String displayChangePasswordForm(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        model.addAttribute("userDTO", userDTO);
        return "profile/change-password";
    }

    @PostMapping("password")
    public String processChangePassForm(UserDTO userDTO, @RequestParam String password2){
        if (userDTO.getPassword().length() > 5 && userDTO.getPassword().equals(password2)) {
           userOperationService.updatePassword(userDTO, password2);
            return "redirect:/profile";
        }
        return "profile/change-password";
    }

    @GetMapping("/donations")
    public String showDonations(Model model, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0);
        model.addAttribute("donations", donationRepository.findAllSortedByArchivedAndPickUpDate(user));
        return "profile/donations";
    }

    @GetMapping("/donation/{id}")
    public String donationDetails(Model model, @PathVariable Long id){
        Optional<Donation> optionalDonation = donationRepository.findById(id);
        optionalDonation.ifPresent(d -> model.addAttribute("donation", d));
        return "profile/donation";
    }

    @GetMapping("archive/{id}")
    public String archiveDonation(@PathVariable Long id) {
        Optional<Donation> optionalDonation = donationRepository.findById(id);
        optionalDonation.ifPresent(d -> {
            d.setArchived(1);
            d.setRealPickUpDate(LocalDate.now());
            d.setRealPickUpTime(LocalTime.now());
            donationRepository.save(d);
        });
        return "redirect:/profile/donations";
    }



}
