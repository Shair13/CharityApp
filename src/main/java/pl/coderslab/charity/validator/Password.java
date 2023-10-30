package pl.coderslab.charity.validator;

import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Password {

    String message() default "{pl.coderslab.charity.validator.password.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
