package pl.coderslab.charity.controller;


import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.DTO.UserDTO;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.SearchUserService;
import pl.coderslab.charity.service.UserService;


@Controller
@RequestMapping("/dashboard")
public class AdminController {

    private final SearchUserService searchUserService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    public AdminController(SearchUserService searchUserService, UserRepository userRepository, UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.searchUserService = searchUserService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/admin/add")
    public String displayAddAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/admin-add-form";
    }

    @PostMapping("/admin/add")
    public String processAddAdminForm(User user, BindingResult result, @RequestParam String password2) {
        if (result.hasErrors() || !user.getPassword().equals(password2)) {
            return "admin/admin-add-form";
        }
        userService.saveUser(user, "ADMIN");
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/admins")
    public String displayAdmins(Model model) {
        model.addAttribute("admins", searchUserService.findUserByRole("ROLE_ADMIN"));
        return "admin/admins";
    }

    @GetMapping("/edit/{id}")
    public String displayEditForm(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        UserDTO userDTO = new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getPassword(),
                user.getEnabled(), user.getRoles());
        model.addAttribute("userDTO", userDTO);
        return "admin/admin-edit-form";
    }

    @PostMapping("/edit")
    public String processEditForm(@Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/admin-edit-form";
        }
        User user = userRepository.findById(userDTO.getId()).orElseThrow(RuntimeException::new);
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(userDTO.getEnabled());
        user.setRoles(userDTO.getRoles());
        userRepository.save(user);
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/password/{id}")
    public String displayChangePassForm(@PathVariable Long id, Model model){
        User user = userRepository.findById(id).orElseThrow(RuntimeException::new);
        UserDTO userDTO = new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getPassword(),
                user.getEnabled(), user.getRoles());
        model.addAttribute("userDTO", userDTO);
        return "admin/change-password";
    }

    @PostMapping("/password")
    public String processChangePassForm(@Valid UserDTO userDTO, @RequestParam String password2){
        if (userDTO.getPassword().length() > 5 && userDTO.getPassword().equals(password2)) {
            User user = userRepository.findById(userDTO.getId()).orElseThrow(RuntimeException::new);
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            userRepository.save(user);
            return "redirect:/dashboard/admins";
        }
        return "admin/change-password";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id){

        userRepository.findById(id).ifPresent(userRepository::delete);
        return "redirect:/dashboard/admins";
    }
}