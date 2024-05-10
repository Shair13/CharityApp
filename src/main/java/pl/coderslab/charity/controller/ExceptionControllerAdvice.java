package pl.coderslab.charity.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.coderslab.charity.exception.DonationNotFoundException;
import pl.coderslab.charity.exception.InstitutionNotFoundException;
import pl.coderslab.charity.exception.UserExistsException;
import pl.coderslab.charity.exception.UserNotFoundException;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(UserExistsException.class)
    public String handleUserExistsException(UserExistsException e, Model model) {
        preparePage(model, e.getMessage(), "/registration");
        return "error/failure-page";
    }
    @ExceptionHandler(DonationNotFoundException.class)
    public String handleUserExistsException(DonationNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }
    @ExceptionHandler(InstitutionNotFoundException.class)
    public String handleUserExistsException(InstitutionNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserExistsException(UserNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }



    void preparePage(Model model, String message, String forward){
        model.addAttribute("errorMessage", message);
        model.addAttribute("urlPart", forward);
    }
}
