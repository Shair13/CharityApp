package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.InstitutionRepository;
import pl.coderslab.charity.repository.RoleRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Service
public class SqlInitService {

    private final RoleRepository roleRepository;
    private final InstitutionRepository institutionRepository;
    private final UserService userService;

    public String sqlInit(Model model) {
        if (roleRepository.findByName("USER") == null) {
            initRoles();
            initInstitutions();
            initAdmin();
            initUser();

            String message = "Zainicjowano bazę danych! :)";
            model.addAttribute("message", message);
            return "home/success-page";
        }
        return "redirect:/";
    }

    private void initRoles() {
        List<String> roles = new ArrayList<>(Arrays.asList("ADMIN_ROLE", "USER_ROLE"));
        roles.forEach(role -> roleRepository.save(new Role(role)));
    }

    private void initInstitutions() {
        List<String> institutions = new ArrayList<>(Arrays.asList(
                "Nowa Nadzieja - Przywracamy drugie życie starym rzeczom!",
                "Hawajska dla każdego - Nie oferujemy pizzy, ale za to chętnie weźmiemy od Ciebie stare ubrania.",
                "Punkt LEGO - Przyjmiemy Lego w każdy stanie.",
                "Mokra Paczka - Pomoc dla powodzian.",
                "Miras LTD - Regenerujemy części za darmo! (Uwaga naprawiają je studenci)"));

        institutions.forEach(inst -> {
            String[] parts = inst.split(" - ");
            Institution institution = new Institution(parts[0], parts[1]);
            institutionRepository.save(institution);
        });
    }

    private void initAdmin() {
        User admin = User.builder()
                .firstName("Admin")
                .email("admin@admin.com")
                .password("123!@#qweQWE")
                .build();
        userService.saveNewUser(admin, "ADMIN");
    }

    private void initUser() {
        User admin = User.builder()
                .firstName("Jango Fett")
                .email("jango.fett@empire.ds")
                .password("123!@#qweQWE")
                .build();
        userService.saveNewUser(admin, "USER");
    }


}
