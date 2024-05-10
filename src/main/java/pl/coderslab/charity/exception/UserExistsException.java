package pl.coderslab.charity.exception;

public class UserExistsException extends RuntimeException{
    public UserExistsException(){
        super("Użytkownik o takim adresie e-mail już ustnieje.");
    }

}
