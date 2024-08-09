package pl.coderslab.charity.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.service.AccountService;
import pl.coderslab.charity.service.UserOperationService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard")
public class AdminController {

    private final UserOperationService userOperationService;
    private final AccountService accountService;

    @GetMapping("/admin/add")
    public String displayAddAdminForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/admin-add-form";
    }

    @PostMapping("/admin/add")
    public String processAddAdminForm(@Valid UserDTO userDTO, BindingResult resultUserDTO,
                                      @Valid PasswordDTO passwordDTO, BindingResult resultPasswordDTO,
                                      @RequestParam char[] password2) {
        if (resultUserDTO.hasErrors() || resultPasswordDTO.hasErrors() || !accountService.comparePasswords(passwordDTO.getPassword(), password2)) {
            return "admin/admin-add-form";
        }
        accountService.saveNewUser(userDTO, passwordDTO, "ADMIN");
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/admins")
    public String displayAdmins(Model model) {
        model.addAttribute("admins", userOperationService.findUsersByRole("ROLE_ADMIN"));
        return "admin/admins";
    }

    @GetMapping("/admin/edit/{id}")
    public String displayEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("userDTO", userOperationService.getUserDTO(id));
        model.addAttribute("roles", accountService.findAllRoles());
        return "admin/admin-edit-form";
    }

    @PostMapping("/admin/edit")
    public String processEditForm(@Valid UserDTO userDTO, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/admin-edit-form";
        }
        userOperationService.updateUserData(userDTO);
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/admin/password/{id}")
    public String displayChangePassForm(@PathVariable Long id, Model model) {
        model.addAttribute("userDTO", userOperationService.getUserDTO(id));
        return "admin/admin-change-password";
    }

    @PostMapping("/admin/password")
    public String processChangePassForm(@Valid PasswordDTO passwordDTO, @RequestParam char[] password2, BindingResult result) {
        if (result.hasErrors() && accountService.comparePasswords(passwordDTO.getPassword(), password2)) {
            return "admin/admin-change-password";
        }
        System.out.println(passwordDTO.getPassword());
        accountService.updatePassword(passwordDTO);
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/admin/delete/{id}")
    public String deleteAdmin(@PathVariable Long id, Authentication authentication) {
        userOperationService.deleteUser(id, authentication);
        return "redirect:/dashboard/admins";
    }

    @GetMapping("/admin/recover/{id}")
    public String recoverUser(@PathVariable Long id) {
        userOperationService.recoverUser(id);
        return "redirect:/dashboard/admins";
    }
}