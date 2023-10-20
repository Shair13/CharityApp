package pl.coderslab.charity;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/registration", "/donation", "/resources/**").permitAll()
                        .anyRequest().authenticated())
                .formLogin((form) -> form
                        .loginPage("/login")
//                       .usernameParameter("email")
                        .permitAll())
                .logout(LogoutConfigurer::permitAll);

        return http.build();
    }

//    @Bean
//    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .requestMatchers("/").permitAll()
//                .requestMatchers("/about").authenticated()
//                .and().formLogin().loginPage("/login");
//        return http.build();
//    }
//
//    @Bean
//    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
//        http.authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/dashboard").authenticated()
//                .antMatchers("/user/**").hasAnyRole("USER", "ADMIN")
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .and().formLogin().loginPage("/login")
//                .defaultSuccessUrl("/")
//                .and().logout().logoutSuccessUrl("/")
//                .and().exceptionHandling().accessDeniedPage("/403");
//        return http.build();
//    }

}
