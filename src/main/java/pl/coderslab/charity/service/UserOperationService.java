package pl.coderslab.charity.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserOperationService {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;


    public UserOperationService(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findUserByRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        return userRepository.findByRoles(role);
    }

    public void updatePassword(UserDTO userDTO, String password2) {
        User user = userRepository.findById(userDTO.getId()).orElseThrow(RuntimeException::new);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        userRepository.save(user);
    }

    public UserDTO getUserDTO(Long userId){
        User user = userRepository.findById(userId).orElseThrow(RuntimeException::new);
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getPassword(),
                user.getEnabled(), user.getRoles());
    }

    public void updateUserData(UserDTO userDTO){
        User user = userRepository.findById(userDTO.getId()).orElseThrow(RuntimeException::new);
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(userDTO.getEnabled());
        user.setRoles(userDTO.getRoles());
        userRepository.save(user);
    }

    public void blockUser(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(u -> {
            u.setEnabled(0);
            userRepository.save(u);
        });
    }

    public void unblockUser(Long userId){
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(u -> {
            u.setEnabled(1);
            userRepository.save(u);
        });
    }

    public void deleteUser(Long userId, Authentication authentication){
        userRepository.findById(userId).ifPresent(u -> {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            if(!u.getEmail().equals(email)){
                u.setIsDeleted(1);
                userRepository.save(u);
            }
        });
    }

    public void recoverUser(Long userId){
        userRepository.findById(userId).ifPresent(u -> {
                u.setIsDeleted(0);
                userRepository.save(u);
        });
    }

    public void activateUser(UUID uuid){
       User user = userRepository.findAllByUuid(uuid);
       user.setEnabled(1);
       userRepository.save(user);
    }
}