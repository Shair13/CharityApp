package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.AccountService;
import pl.coderslab.charity.service.UserOperationService;
import pl.coderslab.charity.service.UserService;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class UserController {

    private final UserOperationService userOperationService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final AccountService accountService;

    @GetMapping("/user/add")
    public String displayAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-add-form";
    }

    @PostMapping("/user/add")
    public String processAddUserForm(User user, BindingResult result, @RequestParam String password2) {
        if (result.hasErrors() || !user.getPassword().equals(password2)) {
            return "admin/user-add-form";
        }
        userService.saveUser(user, "USER");
        return "redirect:/dashboard/users";
    }

    @GetMapping("/users")
    public String displayUsers(Model model) {
        model.addAttribute("users", userOperationService.findUserByRole("ROLE_USER"));
        return "admin/users";
    }

    @GetMapping("/user/edit/{id}")
    public String displayEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("userDTO", userOperationService.getUserDTO(id));
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user-edit-form";
    }

    @PostMapping("/user/edit")
    public String processEditForm(@Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/user-edit-form";
        }
        userOperationService.updateUserData(userDTO);
        return "redirect:/dashboard/users";
    }

    @GetMapping("/user/password/{id}")
    public String displayChangePassForm(@PathVariable Long id, Model model) {
        model.addAttribute("userDTO", userOperationService.getUserDTO(id));
        return "admin/user-change-password";
    }

    @PostMapping("/user/password")
    public String processChangePassForm(@Valid UserDTO userDTO, @RequestParam String password2) {
        if (userDTO.getPassword().length() > 5 && userDTO.getPassword().equals(password2)) {
            accountService.updatePassword(userDTO, password2);
            return "redirect:/dashboard/users";
        }
        return "admin/user-change-password";
    }

    @GetMapping("/user/block/{id}")
    public String blockUser(@PathVariable Long id) {
        userOperationService.blockUser(id);
        return "redirect:/dashboard/users";
    }

    @GetMapping("/user/unblock/{id}")
    public String unblockUser(@PathVariable Long id) {
        userOperationService.unblockUser(id);
        return "redirect:/dashboard/users";
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, Authentication authentication) {
        userOperationService.deleteUser(id, authentication);
        return "redirect:/dashboard/users";
    }

    @GetMapping("/user/recover/{id}")
    public String recoverUser(@PathVariable Long id) {
        userOperationService.recoverUser(id);
        return "redirect:/dashboard/users";
    }
}