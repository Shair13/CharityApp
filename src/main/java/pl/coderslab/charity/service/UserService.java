package pl.coderslab.charity.service;

import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;

import java.util.List;

public interface UserService {

    User findByEmail(String email);

    void saveNewUser(User user, String role);

    List<Role> findAllRoles();
}
