package pl.coderslab.charity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.service.SearchUserService;
import pl.coderslab.charity.service.UserService;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class UserController {

    private final SearchUserService searchUserService;
    private final UserRepository userRepository;
    private final UserService userService;

    public UserController(SearchUserService searchUserService, UserRepository userRepository, UserService userService) {
        this.searchUserService = searchUserService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

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
        model.addAttribute("users", searchUserService.findUserByRole("ROLE_USER"));
        return "admin/users";
    }

    @GetMapping("block/{id}")
    public String blockUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(u -> {
            u.setEnabled(0);
            userRepository.save(u);
        });
        return "redirect:/dashboard/users";
    }

    @GetMapping("unblock/{id}")
    public String unblockUser(@PathVariable Long id) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(u -> {
            u.setEnabled(1);
            userRepository.save(u);
        });
        return "redirect:/dashboard/users";
    }
}
