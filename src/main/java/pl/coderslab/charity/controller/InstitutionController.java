package pl.coderslab.charity.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.repository.InstitutionRepository;

import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class InstitutionController {

    private final InstitutionRepository institutionRepository;

    public InstitutionController(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

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
        institutionRepository.save(institution);
        return "redirect:/dashboard/institutions";
    }


    @GetMapping("/institutions")
    public String displayInstitutions(Model model) {
        model.addAttribute("institutions", institutionRepository.findAllByIsDeleted(0));
        return "admin/institutions";
    }

    @GetMapping("/institution/edit/{id}")
    public String displayEditInstitutionForm(@PathVariable Long id, Model model) {
        Optional<Institution> optionalInstitution = institutionRepository.findById(id);
        optionalInstitution.ifPresent(i -> model.addAttribute("institution", i));
        return "admin/institution-edit-form";
    }

    @PostMapping("/institution/edit")
    public String processEditInstitutionForm(@Valid Institution institution, BindingResult result) {
        if (result.hasErrors()) {
            return "admin/institution-edit-form";
        }
        institutionRepository.save(institution);
        return "redirect:/dashboard/institutions";
    }

    @GetMapping("/institution/delete/{id}")
    public String deleteInstitution(@PathVariable Long id) {
        Optional<Institution> optionalInstitution = institutionRepository.findById(id);
        optionalInstitution.ifPresent(i -> {
            i.setIsDeleted(1);
            institutionRepository.save(i);
        });
        return "redirect:/dashboard/institutions";
    }
}

