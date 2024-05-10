package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final SpringDataUserDetailsService springDataUserDetailsService;

    public List<User> findUserByRole(String roleName) {
        Role role = roleRepository.findByName(roleName);
        return userRepository.findByRoles(role);
    }


    public UserDTO getUserDTO(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User with id = " + userId + " not found."));
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getPassword(),
                user.getEnabled(), user.getRoles());
    }

    public UserDTO getUserDTO(UUID uuid){
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new UserNotFoundException("User not found."));
        return new UserDTO(user.getId(), user.getFirstName(), user.getEmail(), user.getPassword(),
                user.getEnabled(), user.getRoles());
    }

    public void updateUserData(UserDTO userDTO){
        User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new UserNotFoundException("User with id = " + userDTO.getId() + " not found."));
        user.setFirstName(userDTO.getFirstName());
        user.setEmail(userDTO.getEmail());
        user.setEnabled(userDTO.getEnabled());
        user.setRoles(userDTO.getRoles());
        userRepository.save(user);
    }

    public void refreshSession(UserDTO userDTO){
        UserDetails updatedUserDetails = springDataUserDetailsService.loadUserByUsername(userDTO.getEmail());
        Authentication authentication = new UsernamePasswordAuthenticationToken(updatedUserDetails, updatedUserDetails.getPassword(), updatedUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
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
       User user = userRepository.findByUuid(uuid).orElseThrow(UserNotFoundException::new);
       user.setEnabled(1);
       userRepository.save(user);
    }
}