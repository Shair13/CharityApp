package pl.coderslab.charity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.coderslab.charity.repository.UserRepository;
import pl.coderslab.charity.security.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/", "/registration", "/create-user", "/resources/**").permitAll()
                        .requestMatchers("/donation").authenticated()).formLogin(login -> login
                        .loginPage("/login").usernameParameter("email").defaultSuccessUrl("/").permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(exception -> exception.accessDeniedPage("/403"));

        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(UserRepository userRepository){
        return new CustomUserDetailsService(userRepository);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
