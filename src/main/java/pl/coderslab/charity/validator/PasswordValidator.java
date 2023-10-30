package pl.coderslab.charity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        if(!Pattern.compile("[A-Za-z0-9]{8,32}").matcher(s).matches()){
            return false;
        }
        if (!Pattern.compile("[a-z]+").matcher(s).find()) {
            return false;
        }
        if (!Pattern.compile("[A-Z]+").matcher(s).find()) {
            return false;
        }
        if (!Pattern.compile("[0-9]+").matcher(s).find()) {
            return false;
        }
        if (!Pattern.compile("[\\W_]+").matcher(s).find()) {
            return false;
        }

        return true;
    }
}
