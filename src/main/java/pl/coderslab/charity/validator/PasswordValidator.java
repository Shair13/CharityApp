package pl.coderslab.charity.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, char[]> {

    private static final String REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_])[A-Za-z0-9\\W_]{8,32}$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);

    @Override
    public boolean isValid(char[] password, ConstraintValidatorContext constraintValidatorContext) {

        if (password == null || password.length == 0) {
            return false;
        }

        boolean isPasswordCorrect;

        String stringPassword = new String(password);
        isPasswordCorrect = PATTERN.matcher(stringPassword).matches();
        stringPassword = null;

        return isPasswordCorrect;
    }
}