package pl.coderslab.charity.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + email + " not found!"));

        final String password = user.getPassword();

        final Collection<? extends GrantedAuthority> authorities =
                user
                        .getRoles()
                        .stream()
                        .map(Role::getName)
                        .map(SimpleGrantedAuthority::new)
                        .toList();

        final boolean accountNonExpired = true;
        final boolean accountNonLocked = true;
        final boolean credentialsNonExpired = true;
        final boolean enabled = true;

        return new CustomUserDetails(email, password, authorities, accountNonExpired,
                accountNonLocked, credentialsNonExpired, enabled);
    }
}
