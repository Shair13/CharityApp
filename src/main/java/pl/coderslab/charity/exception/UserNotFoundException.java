package pl.coderslab.charity.exception;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Nie znaleziono użytkownika.");
    }

    public UserNotFoundException(String email) {
        super("Użytkownik " + email + " nie istnieje.");
    }

    public UserNotFoundException(Long id) {
        super("Użytkownik z id = " + id + " nie istnieje.");
    }
}
