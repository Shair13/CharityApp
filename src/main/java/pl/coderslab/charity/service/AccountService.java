package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.UserExistsException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final UserService userService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SpringDataUserDetailsService springDataUserDetailsService;


    public String createAccount(User user) {
        checkEmailExists(user);
        userService.saveNewUser(user, "USER");
        String link = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link aktywacyjny do Twojego konta: " + link + " - kliknij, aby aktywować konto i móc się zalogować.";
        emailService.sendSimpleMessage(user.getEmail(), "Account confirmation", emailText);

        return "Udało sie! Zajrzyj na maila, tam znajdziesz link aktywacyjny do Twojego konta.";
    }

    public boolean comparePasswords(String password, String repeatedPassword) {
        return password.equals(repeatedPassword);
    }

    public String remindPassword(User user, String email) {
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userRepository.save(user);
        String link = "http://localhost:8080/newpass/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link do zresetowania hasła: " + link + " - kliknij, aby przejść do formularza i ustawić nowe hasło.";
        emailService.sendSimpleMessage(email, "Reset password", emailText);

        return "Udało sie! Zajrzyj na maila, tam znajdziesz link do formularza zmiany hasła.";
    }

    public void updatePassword(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() ->
                new UserNotFoundException("Użytkownik z id = " + userDTO.getId() + " nie istnieje."));
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }

    public UserDTO displayCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(UserNotFoundException::new);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        return userDTO;
    }

    public void updateUser(UserDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        optionalUser.ifPresent(u -> {
            u.setEmail(userDTO.getEmail());
            u.setFirstName(userDTO.getFirstName());
            userRepository.save(u);
            UserDetails updatedUserDetails = springDataUserDetailsService.loadUserByUsername(userDTO.getEmail());
            Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        });
    }

    void checkEmailExists(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserExistsException();
        }
    }
}
