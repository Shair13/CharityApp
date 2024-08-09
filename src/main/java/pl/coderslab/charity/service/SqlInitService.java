package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.coderslab.charity.dto.PasswordDTO;
import pl.coderslab.charity.dto.UserDTO;
import pl.coderslab.charity.model.Category;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.model.Role;
import pl.coderslab.charity.model.User;
import pl.coderslab.charity.repository.CategoryRepository;
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
    private final CategoryRepository categoryRepository;
    private final AccountService accountService;

    public String sqlInit(Model model) {

        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            initRoles();
            initInstitutions();
            initCategories();
            initAdmin();
//            initUser();

            String message = "Zainicjowano bazę danych! :)";
            model.addAttribute("message", message);
            return "home/success-page";
        }
        return "redirect:/";
    }

    private void initRoles() {
        List<String> roles = new ArrayList<>(Arrays.asList("ROLE_ADMIN", "ROLE_USER"));
        roles.forEach(role -> roleRepository.save(new Role(role)));
    }

    private void initCategories() {
        List<String> categories = List.of("Koszulki", "Spodnie", "Bluzy", "Tekstylia", "Zabawki", "Elektronika", "Artykuły metalowe");

        categories.forEach(categoryName -> {
            Category category = new Category(categoryName);
            categoryRepository.save(category);
        });
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
        char[] password = {'1', '2', '3', '!', '@', '#', 'q', 'w', 'e', 'Q', 'W', 'E'};
        PasswordDTO passwordDTO = new PasswordDTO(password);
        UserDTO admin = new UserDTO("Admin", "admin@admin.com", 1);
        accountService.saveNewUser(admin, passwordDTO, "ADMIN");
    }

    private void initUser() {
        char[] password = {'1', '2', '3', '!', '@', '#', 'q', 'w', 'e', 'Q', 'W', 'E'};
        PasswordDTO passwordDTO = new PasswordDTO(password);
        UserDTO user = new UserDTO("Jango Fett", "jango.fett@empire.ds", 1);
        accountService.saveNewUser(user, passwordDTO, "USER");
    }
}
