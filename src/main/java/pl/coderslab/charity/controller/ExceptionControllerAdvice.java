package pl.coderslab.charity.controller;

import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.coderslab.charity.exception.DonationNotFoundException;
import pl.coderslab.charity.exception.InstitutionNotFoundException;
import pl.coderslab.charity.exception.UserExistsException;
import pl.coderslab.charity.exception.UserNotFoundException;

import java.util.List;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(UserExistsException.class)
    public String handleUserExistsException(UserExistsException e, Model model) {
        preparePage(model, e.getMessage(), "/registration");
        return "error/failure-page";
    }

    @ExceptionHandler(DonationNotFoundException.class)
    public String handleDonationNotFoundException(DonationNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }

    @ExceptionHandler(InstitutionNotFoundException.class)
    public String handleInstitutionNotFoundException(InstitutionNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e, Model model) {
        preparePage(model, e.getMessage(), "/");
        return "error/failure-page";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(MethodArgumentNotValidException e, Model model) {
        preparePage(model, extractMessage(e), "/");
        return "error/failure-page";
    }


    void preparePage(Model model, String message, String forward) {
        model.addAttribute("errorMessage", message);
        model.addAttribute("urlPart", forward);
    }

    private String extractMessage(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        if (!fieldErrors.isEmpty()) {
            FieldError error = fieldErrors.get(0);
            return error.getDefaultMessage();
        }
        return "An unknown error occurred";
    }
}
