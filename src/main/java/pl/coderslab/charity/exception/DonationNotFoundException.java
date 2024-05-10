package pl.coderslab.charity.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DonationNotFoundException extends RuntimeException{

    public DonationNotFoundException(String message){
        super(message);
    }

    public DonationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
