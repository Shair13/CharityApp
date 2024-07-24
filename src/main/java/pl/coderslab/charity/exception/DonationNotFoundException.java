package pl.coderslab.charity.exception;

public class DonationNotFoundException extends RuntimeException{
    public DonationNotFoundException(Long id){
        super("Darowizna z id = " + id + " nie istnieje.");
    }
}
