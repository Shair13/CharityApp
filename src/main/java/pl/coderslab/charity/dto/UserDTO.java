package pl.coderslab.charity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.validator.Password;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;
    @NotBlank(message = "Pole nie może być puste")
    private String firstName;
    @NotBlank(message = "Pole nie może być puste")
    private String email;
    private Set<Role> roles;
    private int enabled;

    public UserDTO(String firstName, String email, int enabled) {
        this.firstName = firstName;
        this.email = email;
        this.enabled = enabled;
    }
}
