package pl.coderslab.charity.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import pl.coderslab.charity.DTO.UserDTO;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.SpringDataUserDetailsService;

import java.util.Optional;

@Controller
@RequestMapping("/profile")
public class UserProfileController {

    private final UserRepository userRepository;
    private final SpringDataUserDetailsService springDataUserDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;



    public UserProfileController(UserRepository userRepository, SpringDataUserDetailsService springDataUserDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.springDataUserDetailsService = springDataUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/edit")
    public String displayEditUserForm(Model model, Authentication authentication){
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
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
        User user = userRepository.findByEmail(email);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        model.addAttribute("userDTO", userDTO);
        return "profile/change-password";
    }

    @PostMapping("password")
    public String processChangePassForm(UserDTO userDTO, @RequestParam String password2){
        if (userDTO.getPassword().length() > 5 && userDTO.getPassword().equals(password2)) {
            Optional<User> optionalUser = userRepository.findById(userDTO.getId());
            optionalUser.ifPresent(u -> {
                u.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                userRepository.save(u);
            });
            return "redirect:/profile";
        }
        return "profile/change-password";
    }



}
