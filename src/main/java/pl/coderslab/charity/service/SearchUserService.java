package pl.coderslab.charity.service;

import org.springframework.stereotype.Service;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.RoleRepository;
import pl.coderslab.charity.repository.UserRepository;

import java.util.List;

@Service
public class SearchUserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public SearchUserService(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    public List<User> findUserByRole(String roleName){
        Role role = roleRepository.findByName(roleName);
        return userRepository.findByRoles(role);
    }

}
