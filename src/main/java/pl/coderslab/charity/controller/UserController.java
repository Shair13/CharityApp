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



    @GetMapping("/users")
    public String displayUsers(Model model) {
        model.addAttribute("users", searchUserService.findUserByRole("ROLE_USER"));
        return "admin/users";
    }

    @GetMapping("/user/edit/{id}")
    public String displayEditUserForm(@PathVariable Long id, Model model) {
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(u -> model.addAttribute("user", u));
        return "admin/user-edit-form";
    }


}
