package pl.coderslab.charity.service;

import pl.coderslab.charity.model.User;

public interface UserService {

    User findByEmail(String email);

    void saveUser(User user);
}
