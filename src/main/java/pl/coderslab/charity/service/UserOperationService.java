package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.RoleNotFoundException;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserOperationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<User> findUsersByRole(String roleName) {
        Role role = roleRepository.findByName(roleName).orElseThrow(RoleNotFoundException::new);
        return userRepository.findByRoles(role);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(() -> new UserNotFoundException("Nie znaleziono użytkownika z emailem: " + email + "."));
    }

    public UserDTO getUserDTO(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Użytkownik z id = " + userId + " nie istnieje."));
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getRoles(), user.getEnabled());
    }

    public UserDTO getUserDTO(UUID uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getRoles(), user.getEnabled());
    }

    public void updateUserData(UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new UserNotFoundException("Użytkownik z id = " + userDTO.getId() + " nie istnieje."));
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setRoles(userDTO.getRoles());
        user.setEnabled(userDTO.getEnabled());
        userRepository.save(user);
    }

    public void blockUserToggle(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Użytkownik z id = " + userId + " nie istnieje."));
        if (user.getEnabled() == 1) {
            user.setEnabled(0);
        } else {
            user.setEnabled(1);
        }
        userRepository.save(user);
    }

    public void deleteUser(Long userId, Authentication authentication) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Użytkownik z id = " + userId + " nie istnieje."));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String email = userDetails.getUsername();
        if (!user.getEmail().equals(email)) {
            user.setIsDeleted(1);
            userRepository.save(user);
        }
    }

    public void recoverUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Użytkownik z id = " + userId + " nie istnieje."));
        user.setIsDeleted(0);
        userRepository.save(user);
    }

    public void activateUser(UUID uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        user.setEnabled(1);
        userRepository.save(user);
    }
}