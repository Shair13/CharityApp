package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailServiceImpl emailService;
    private final BCryptPasswordEncoder passwordEncoder;


    public void createAccount(User user, Model model){
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userService.saveNewUser(user, "USER");
        String link = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link aktywacyjny do Twojego konta: " + link + " - kliknij, aby aktywować konto i móc się zalogować.";
        emailService.sendSimpleMessage(user.getEmail(), "Account confirmation", emailText);

        String message = "Udało sie! Zajrzyj na maila, tam znajdziesz link aktywacyjny do Twojego konta.";
        model.addAttribute("message", message);
    }

    public boolean comparePasswords(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }

    public void remindPassword(User user, String email, Model model){
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userRepository.save(user);
        String link = "http://localhost:8080/newpass/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link do zresetowania hasła: " + link + " - kliknij, aby przejść do formularza i ustawić nowe hasło.";
        emailService.sendSimpleMessage(email, "Reset password", emailText);

        String message = "Udało sie! Zajrzyj na maila, tam znajdziesz link do formularza zmiany hasła.";
        model.addAttribute("message", message);
    }

    public void updatePassword(UserDTO userDTO, String password2) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new UserNotFoundException("User with id = " + userDTO.getId() + " not found.")); //swój wyjątek (userNotFoundException) - exception Handler (spring MVC)
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }
}
