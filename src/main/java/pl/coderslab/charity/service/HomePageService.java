package pl.coderslab.charity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import pl.coderslab.charity.model.Institution;
import pl.coderslab.charity.repository.DonationRepository;
import pl.coderslab.charity.repository.InstitutionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomePageService {

    private final EmailService emailService;
    private final InstitutionRepository institutionRepository;
    private final DonationRepository donationRepository;


    public List<Institution> findAllInstitutions() {
        return institutionRepository.findAll();
    }

    public int bagsQuantity() {
        Integer bagsQuantity = donationRepository.sumQuantity();
        return bagsQuantity == null ? 0 : bagsQuantity;
    }

    public int donationsQuantity() {
        Integer donationsQuantity = donationRepository.sumQuantity();
        return donationsQuantity == null ? 0 : donationsQuantity;
    }

    public void sendMessage(String name, String surname, String message, Model model) {
        String emailText = "Message from " + name + " " + surname + ": " + message;
        emailService.sendSimpleMessage("charityapp2024@gmail.com", "Message from customer", emailText);
        String successMessage = "Wiadomość została wysłana. Odpowiemy najszybciej jak to tylko możliwe.";
        model.addAttribute("message", successMessage);
    }
}