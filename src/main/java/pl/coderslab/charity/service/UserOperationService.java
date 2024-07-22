package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.exception.UserNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserOperationService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public List<User> findUserByRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        return userRepository.findByRoles(role);
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndIsDeleted(email, 0).orElseThrow(UserNotFoundException::new);
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

    public void blockUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(u -> {
            u.setEnabled(0);
            userRepository.save(u);
        });
    }

    public void unblockUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(u -> {
            u.setEnabled(1);
            userRepository.save(u);
        });
    }

    public void deleteUser(Long userId, Authentication authentication) {
        userRepository.findById(userId).ifPresent(u -> {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            if (!u.getEmail().equals(email)) {
                u.setIsDeleted(1);
                userRepository.save(u);
            }
        });
    }

    public void recoverUser(Long userId) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setIsDeleted(0);
            userRepository.save(u);
        });
    }

    public void activateUser(UUID uuid) {
        User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
        user.setEnabled(1);
        userRepository.save(user);
    }
}