package pl.coderslab.charity.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.validation.annotation.Validated;
import pl.coderslab.charity.validator.Password;

import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Validated
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Long id;

    @NotBlank(message = "Pole nie może być puste")
    private String firstName;

    @NotBlank(message = "Pole nie może być puste")
    private String email;

    @NotBlank(message = "Pole nie może być puste")
    @Password(message = "Hasło musi posiadać co najmniej jedną małą literę, jedną wielką literę, jedną cyfrę i jeden znak specjalny. Długość hasła powinna być większa niż 8 i mniejsza niż 32 znaki.")
    private String password;

    private int enabled = 0;

    private int isDeleted = 0;

    private UUID uuid = UUID.randomUUID();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    public User(String firstName, String email, String password, int enabled) {
        this.firstName = firstName;
        this.email = email;
        this.password = password;
        this.enabled = enabled;
    }
}
