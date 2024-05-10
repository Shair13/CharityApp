package pl.coderslab.charity.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException(){
        super("Nie znaleziono użytkownika.");
    }

    public UserNotFoundException(String message){
        super(message);
    }
}
