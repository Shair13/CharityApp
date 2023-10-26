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

    public UserController(SearchUserService searchUserService, UserRepository userRepository) {
        this.searchUserService = searchUserService;
        this.userRepository = userRepository;
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
