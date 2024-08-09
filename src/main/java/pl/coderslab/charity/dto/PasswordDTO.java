package pl.coderslab.charity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.coderslab.charity.validator.Password;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDTO {
    private Long id;
    @Password(message = "Hasło musi posiadać co najmniej jedną małą literę, jedną wielką literę, jedną cyfrę i jeden znak specjalny. Długość hasła powinna być większa niż 8 i mniejsza niż 32 znaki.")
    private char[] password;

    public PasswordDTO(char[] password) {
        this.password = password;
    }
}
