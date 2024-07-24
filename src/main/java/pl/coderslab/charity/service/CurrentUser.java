package pl.coderslab.charity.service;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CurrentUser extends User {
    private final pl.coderslab.charity.model.User user;

    public CurrentUser(String email, String password,
                       Collection<? extends GrantedAuthority> authorities,
                       pl.coderslab.charity.model.User user) {
        super(email, password, authorities);
        this.user = user;
    }

    @Override
    public boolean isEnabled() {
        return getUser().getEnabled() == 1;
    }
}