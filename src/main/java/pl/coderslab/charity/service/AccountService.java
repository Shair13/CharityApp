package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.RoleNotFoundException;
import pl.coderslab.charity.exception.UserExistsException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final SpringDataUserDetailsService springDataUserDetailsService;


    public User saveNewUser(UserDTO userDTO, PasswordDTO passwordDTO, String role) {
        checkEmailExists(userDTO.getEmail());
        User user = createUser(userDTO, passwordDTO, role);
        return userRepository.save(user);
    }

    public String createAccount(UserDTO userDTO, PasswordDTO passwordDTO) {
        checkEmailExists(userDTO.getEmail());
        User user = saveNewUser(userDTO, passwordDTO, "USER");
        String link = "http://localhost:8080/activation/" + user.getUuid();
        String emailText = "Cześć " + user.getFirstName() + ", oto link aktywacyjny do Twojego konta: " + link + " - kliknij, aby aktywować konto i móc się zalogować.";
        emailService.sendSimpleMessage(user.getEmail(), "Account confirmation", emailText);

        return "Udało się! Zajrzyj na swojego maila (" + user.getEmail() + "), tam znajdziesz link aktywacyjny do Twojego konta.";
    }

    public UserDTO displayCurrentUser(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        User user = userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(() -> new UserNotFoundException(email));
        return getUserDTO(user);
    }

    public boolean comparePasswords(char[] password, char[] repeatedPassword) {
        return Arrays.equals(password, repeatedPassword);
    }

    public String remindPassword(User user, String email) {
        UUID uuid = UUID.randomUUID();
        user.setUuid(uuid);
        userRepository.save(user);
        String link = "http://localhost:8080/newpass/" + uuid;
        String emailText = "Cześć " + user.getFirstName() + ", oto link do zresetowania hasła: " + link + " - kliknij, aby przejść do formularza i ustawić nowe hasło.";
        emailService.sendSimpleMessage(email, "Reset password", emailText);

        return "Udało sie! Zajrzyj na maila, tam znajdziesz link do formularza zmiany hasła.";
    }

    public void updatePassword(PasswordDTO passwordDTO) {
        User user = userRepository.findById(passwordDTO.getId()).orElseThrow(() ->
                new UserNotFoundException(passwordDTO.getId()));
        user.setPassword(encodePassword(passwordDTO.getPassword()));
        userRepository.save(user);
    }

    public void updateUser(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new UserNotFoundException(userDTO.getId()));
        user.setEmail(userDTO.getEmail());
        user.setFirstName(userDTO.getFirstName());
        userRepository.save(user);
        UserDetails updatedUserDetails = springDataUserDetailsService.loadUserByUsername(userDTO.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    void checkEmailExists(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserExistsException(email);
        }
    }

    public List<Role> findAllRoles() {
        return roleRepository.findAll();
    }

    private String encodePassword(char[] password) {
        String encodedPassword;
        try {
            String stringPassword = new String(password);
            encodedPassword = passwordEncoder.encode(stringPassword);
            stringPassword = null;
        } finally {
            Arrays.fill(password, '0');
        }
        return encodedPassword;
    }

    private User createUser(UserDTO userDTO, PasswordDTO passwordDTO, String role){
        User user = new User();
        user.setEnabled(userDTO.getEnabled());
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setPassword(encodePassword(passwordDTO.getPassword()));
        Role userRole = roleRepository.findByName("ROLE_" + role).orElseThrow(RoleNotFoundException::new);
        user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
        return user;
    }

    private UserDTO getUserDTO(User user){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setEnabled(user.getEnabled());
        return userDTO;
    }
}
