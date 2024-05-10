package pl.coderslab.charity.exception;

public class DonationNotFoundException extends RuntimeException{
    public DonationNotFoundException(){
        super("Nie znaleziono darowizny.");
    }

}
