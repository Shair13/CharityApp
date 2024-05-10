package pl.coderslab.charity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class InstitutionNotFoundException extends RuntimeException{

    public InstitutionNotFoundException(String message){
        super(message);
    }

    public InstitutionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
