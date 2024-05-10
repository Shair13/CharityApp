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
    @NotBlank(message = "Pole nie może być puste")
    @Password(message = "Hasło musi posiadać co najmniej jedną małą literę, jedną wielką literę, jedną cyfrę i jeden znak specjalny. Długość hasła powinna być większa niż 8 i mniejsza niż 32 znaki.")    private String password;
    private Set<Role> roles;
}
