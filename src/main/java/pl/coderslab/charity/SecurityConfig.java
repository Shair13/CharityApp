package pl.coderslab.charity;

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
                        .requestMatchers("/", "/registration", "/create-user", "/resources/**").permitAll()
                        .requestMatchers("/donation").authenticated()).formLogin(f -> f
                        .loginPage("/login").usernameParameter("email").defaultSuccessUrl("/").permitAll())
                .logout(l -> l.logoutSuccessUrl("/").permitAll())
                .exceptionHandling(e -> e.accessDeniedPage("/403"));

        return http.build();
    }


    //   .authorizeHttpRequests((requests) -> requests
    //                        .requestMatchers("/", "/registration", "/create-user", "/resources/**").permitAll()
    //                        .requestMatchers("/donation").authenticated())
    //                .formLogin(form -> form
    //                        .loginPage("/login").usernameParameter("email").defaultSuccessUrl("/")
    //                        .permitAll())
    //                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
    //                .exceptionHandling(exception -> exception.accessDeniedPage("/403"));

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
