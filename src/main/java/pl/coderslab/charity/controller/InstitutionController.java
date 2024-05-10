package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.service.InstitutionService;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class InstitutionController {

    private final InstitutionService institutionService;

    @GetMapping("/institution/add")
    public String displayAddForm(Model model) {
        model.addAttribute("institution", new Institution());
        return "admin/institution-add-form";
    }

    @PostMapping("/institution/add")
    public String processAddForm(@Valid Institution institution, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/institution-add-form";
        }
        institutionService.saveInstitution(institution);
        return "redirect:/dashboard/institutions";
    }


    @GetMapping("/institutions")
    public String displayInstitutions(Model model) {
        model.addAttribute("institutions", institutionService.findAllInstitutions());
        return "admin/institutions";
    }

    @GetMapping("/institution/edit/{id}")
    public String displayEditInstitutionForm(@PathVariable Long id, Model model) {
        model.addAttribute("institution", institutionService.findInstitutionById(id));
        return "admin/institution-edit-form";
    }

    @PostMapping("/institution/edit")
    public String processEditInstitutionForm(@Valid Institution institution, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/institution-edit-form";
        }
        institutionService.saveInstitution(institution);
        return "redirect:/dashboard/institutions";
    }

    @GetMapping("/institution/delete/{id}")
    public String deleteInstitution(@PathVariable Long id) {
        institutionService.deleteInstitution(id);
        return "redirect:/dashboard/institutions";
    }
}

