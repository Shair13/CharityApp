package pl.coderslab.charity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.coderslab.charity.validator.Password;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private Long id;
    @NotBlank(message = "Pole nie może być puste")
    @Password(message = "Hasło musi posiadać co najmniej jedną małą literę, jedną wielką literę, jedną cyfrę i jeden znak specjalny. Długość hasła powinna być większa niż 8 i mniejsza niż 32 znaki.")
    private String password;

}
