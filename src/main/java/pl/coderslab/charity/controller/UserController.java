package pl.coderslab.charity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
//    @GetMapping("/create-user")
//    @ResponseBody
//    public String createUser() {
//        User user = new User();
//        user.setFirstName("Czarek");
//        user.setEmail("admin@admin.pl");
//        user.setPassword("admin");
//        userService.saveUser(user);
//        return "admin";
//    }

    @GetMapping("/registration")
    public String displayRegistrationForm(Model model){
        model.addAttribute("user", new User());
        return "home/registration-form";
    }

    @PostMapping("/registration")
    public String processRegistrationForm(User user, BindingResult result, @RequestParam String password2){
        if(result.hasErrors() || !user.getPassword().equals(password2)){
            return "home/registration-form";
        }
        userService.saveUser(user);
        return "redirect:/";
    }


}
