package pl.coderslab.charity.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(r -> r
                        .requestMatchers("/", "/registration", "/resources/**", "/activation/**", "/reminder/**", "/forbidden", "/newpass/**").permitAll()
                        .requestMatchers("/donation", "/profile/**", "/confirmation").authenticated()
                        .requestMatchers("/dashboard/**").hasRole("ADMIN"))
                .formLogin(f -> f.loginPage("/login").usernameParameter("email").defaultSuccessUrl("/").permitAll())
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(e -> e.accessDeniedPage("/forbidden"));

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
