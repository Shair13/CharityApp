package pl.coderslab.charity.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("home/login-form");
        registry.addViewController("/dashboard").setViewName("admin/dashboard");
        registry.addViewController("/profile").setViewName("profile/profile");
        registry.addViewController("/confirmation").setViewName("app/form-confirmation");
        registry.addViewController("/forbidden").setViewName("error/access-denied");
    }
}
